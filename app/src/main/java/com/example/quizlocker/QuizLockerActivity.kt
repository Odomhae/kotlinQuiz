package com.example.quizlocker

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_quiz_locker.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class QuizLockerActivity : AppCompatActivity() {

    var quiz :JSONObject? = null

    //정답 횟수 저장
    val correctAnswerPref by lazy { getSharedPreferences("correctAnswer", Context.MODE_PRIVATE) }
    //오류 횟수 저장
    val wrongAnswerPref by lazy { getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 잠금화면보다
        // 버전에 따라 다르게 설정
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            // 잠금화면에서 보여지도록 설정
            setShowWhenLocked(true)
            // 잠금해제
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager //KeyguardManager 자료형
            keyguardManager.requestDismissKeyguard(this, null)
        }else{
            // 잠금화면에서 보여지도록 설정
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            //기본 잠금화면 해제
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        // 화면 켜진 상태로 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_quiz_locker)

        // 퀴즈 데이터 가져옴
        // asset 폴더의 capital.json 파일
        val json = assets.open("capital.json").reader().readText()
        val quizArray = JSONArray(json)

        // 랜덤으로 퀴즈 선택
        quiz = quizArray.getJSONObject(Random.nextInt(quizArray.length()))
        //퀴즈 보여줌
        quizLabel.text = quiz?.getString("question")
        choice1.text = quiz?.getString("choice1")
        choice2.text = quiz?.getString("choice2")

        // 정답, 오답 횟수 보여줌
        // id별로 횟수 다름
        val id = quiz?.getInt("id").toString()
        correctCountLabel.text = "정답횟수 : ${correctAnswerPref.getInt(id, 0)}"
        wrongCountLabel.text = "오답횟수 : ${wrongAnswerPref.getInt(id, 0)}"


        //seekbar 변경시 리스너
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when{
                    //우측 끝
                    progress >95 -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.unlock)
                    }
                    // 왼쪽 끝
                    progress <5 ->{
                        rightImageView.setImageResource(R.drawable.padlock)
                        leftImageView.setImageResource(R.drawable.unlock)
                    }
                    else -> {
                        rightImageView.setImageResource(R.drawable.padlock)
                        leftImageView.setImageResource(R.drawable.padlock)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            // 터지 조작 다 끝낸 경우
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?:50 // 널값을 허용하지 않는 변수에 널 값이 들어 갔을때 50으로

                when{
                    progress >95 -> checkChoice(quiz?.getString("choice2") ?: "")
                    progress < 5 -> checkChoice(quiz?.getString("choice1") ?: "")

                    else -> seekBar?.progress = 50
                }
            }
        })


    }
    
    // 정답 체크 함수
    fun checkChoice(choice : String){
        quiz?.let {
            when{
                //정답이면 정답 횟수 늘려주고 액티비티 종료
                choice == it.getString("answer") ->{
                    val id = it.getInt("id").toString()
                    //id에 해당하는 정답횟수 가져옴
                    var count = correctAnswerPref.getInt(id, 0)
                    count++
                    correctAnswerPref.edit().putInt(id, count).apply()
                    correctCountLabel.text = "정답횟수 : ${count}"

                    finish()
                }
                else ->{

                    val id = it.getInt("id").toString()
                    var count = wrongAnswerPref.getInt(id, 0)
                    count++
                    wrongAnswerPref.edit().putInt(id, count).apply()
                    wrongCountLabel.text = "오답횟수 : ${count}"


                    //정답 아니면 ui 초기화
                    rightImageView.setImageResource(R.drawable.padlock)
                    leftImageView.setImageResource(R.drawable.padlock)
                    seekBar?.progress = 50

                    // 진동 추가
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    // SDk 버전에 따라
                    if(Build.VERSION.SDK_INT >= 26){
                        // 0.5초동안 100의 세기로 진동
                        vibrator.vibrate(VibrationEffect.createOneShot(500, 100))
                    }else{
                        vibrator.vibrate(1000)
                    }

                }
            }
        }
    }
}
