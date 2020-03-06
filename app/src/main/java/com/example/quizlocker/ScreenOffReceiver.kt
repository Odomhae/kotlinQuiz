package com.example.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Log.d
import android.widget.Toast

class ScreenOffReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when{
            intent?.action ==Intent.ACTION_SCREEN_OFF ->{
                Log.d("eeee", "화면꺼짐")
                Toast.makeText(context, "화면꺼짐 ",Toast.LENGTH_LONG).show()

                //화면꺼지면 quizlockerActivity 실행
                val intent = Intent(context, QuizLockerActivity::class.java)
                //새로운 액티비티로 실행
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // 기존 액티비티 스택 제거
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                context?.startActivity(intent)
            }


        }
    }
}