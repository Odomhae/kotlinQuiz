package com.example.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when{
            // 부팅 완료시 메시지 확인
            intent?.action == Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("qqq", "부팅완료")
                print("qnxldghka완료 ")

                context?.let{
                    // 퀴즈잠금화면 설정값이 On인지 확인
                    val pref = PreferenceManager.getDefaultSharedPreferences(context)
                    val useLockScreen = pref.getBoolean("useLockScreen", false)
                    if(useLockScreen){
                        //서비스 시작
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            it.startForegroundService(Intent(context, LockScreenService::class.java))
                        }else{
                            it.startService(Intent(context, LockScreenService::class.java))
                        }
                    }

                }
            }
        }
    }
}