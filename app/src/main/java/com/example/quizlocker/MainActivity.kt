package com.example.quizlocker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.MultiSelectListPreference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashSet

class MainActivity : AppCompatActivity() {

    val fragment = MyPreferenceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager.beginTransaction().replace(R.id.preferenceContent, fragment).commit()

        //버튼 클릭되면 오답, 정답 초기화
        initButton.setOnClickListener { initAnswerCount() }
    }

    // 정답 오답 초기화
    fun initAnswerCount(){
        // 정답 오답횟수의 설정정보 가져옴
        val correctAnswerPref = getSharedPreferences("correctAnswer", Context.MODE_PRIVATE)
        val wrongAnswerPref = getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE)

        // 초기화
        correctAnswerPref.edit().clear().apply()
        wrongAnswerPref.edit().clear().apply()
    }

    class MyPreferenceFragment : PreferenceFragment(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            //환경설정 리소스 파일
            addPreferencesFromResource(R.xml.pref)

            // 퀴즈 종류 요약정보에 현재 선택된 항목 보여줌
            val categoryPref = findPreference("category") as MultiSelectListPreference
            categoryPref.summary = categoryPref.values.joinToString(", ")

            // 환경설정 정보값이 변경되면 요약정보도 같이 변경되게 리스터 등록
            categoryPref.setOnPreferenceChangeListener { preference, newValue ->
                val newValueSet = newValue as? HashSet<*>
                    ?: return@setOnPreferenceChangeListener true

                categoryPref.summary = newValue.joinToString(", ")

                true
            }

            // 퀴즈 잠금화면 사용 스위치 객체 사용
            val useLockScreenPref = findPreference("useLockScreen") as SwitchPreference
            // 클릭됬을때
            useLockScreenPref.setOnPreferenceClickListener {
                when{
                    // 퀴즈 잠금화면 사용이 체크된 경우 lockScreenService 실행
                    useLockScreenPref.isChecked ->{
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            activity.startForegroundService(Intent(activity, LockScreenService::class.java))
                            }else{
                            activity.startService(Intent(activity, LockScreenService::class.java))
                        }
                    }
                    // 사용 체크 안됬으면 서비스 중단
                    else -> activity.stopService(Intent(activity, LockScreenService::class.java))
                }
                true
            }

            // 앱이 시작됬을대 이미 퀴즈잠금화면 사용이 체크되어있으면 서비스 실행
            if(useLockScreenPref.isChecked){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    activity.startForegroundService(Intent(activity, LockScreenService::class.java))
                }else{
                    activity.startService(Intent(activity, LockScreenService::class.java))
                }

            }

        }
    }
}
