package com.ssafy.smartstore_jetpack

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.smartstore_jetpack.base.BaseActivity
import com.ssafy.smartstore_jetpack.ui.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { message ->
            val messageTitle = message.title ?: "Default Title"
            val messageContent = message.body ?: "Default Content"

            // Intent 설정
            val mainIntent = Intent(this, BaseActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // PendingIntent 생성
            val mainPendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val channelId = "your_channel_id"
            val channelName = "Custom Notification Channel"
            val soundUri = Uri.parse("android.resource://${packageName}/raw/notification_sound")

            // Android 8.0 이상에서 알림 채널 생성
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                    setSound(soundUri, audioAttributes) // 커스텀 사운드 설정
                }

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)
            }

            // 알림 빌더 생성
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.box) // 알림 아이콘
                .setContentTitle(messageTitle) // 알림 제목
                .setContentText(messageContent) // 알림 내용
                .setAutoCancel(true) // 클릭 시 알림 제거
                .setContentIntent(mainPendingIntent) // 클릭 시 Intent 실행
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 우선순위 설정
                .setSound(soundUri) // 커스텀 사운드 설정

            // 권한 확인 및 알림 표시
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(101, builder.build())
            } else {
                Log.e("MyFirebaseMessaging", "POST_NOTIFICATIONS 권한이 없습니다.")
            }
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // 서버에 토큰 갱신 로직 추가
    }


}
