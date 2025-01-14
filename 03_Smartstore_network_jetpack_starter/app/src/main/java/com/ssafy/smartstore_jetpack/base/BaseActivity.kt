package com.ssafy.smartstore_jetpack.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

// 액티비티의 기본을 작성, 뷰 바인딩 활용
private const val TAG = "BaseActivity_싸피"
abstract class BaseActivity<B : ViewBinding>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {
    protected lateinit var binding: B
        private set

    // 뷰 바인딩 객체를 받아서 inflate해서 화면을 만들어줌.
    // 즉 매번 onCreate에서 setContentView를 하지 않아도 됨.
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        //FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if(!task.isSuccessful) {
                    Log.d(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                    return@OnCompleteListener
                }
                // token log 남기기
                Log.d(TAG, "token: ${task.result?: "task.result is null"}")
            })
        createNotificationChannel(channel_id, "ssafy")

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String) {
        val soundUri = Uri.parse("android.resource://${packageName}/raw/notification_sound")
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(soundUri, attributes) // 채널에 커스텀 소리 설정
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    // 토스트를 쉽게 띄울 수 있게 해줌.
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        const val channel_id = "ssafy_channel"
    }

}