package com.lepitar.corvid19.Alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.lepitar.corvid19.R
import com.lepitar.corvid19.ui.SplashActivity
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, SplashActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0)
        val builder = NotificationCompat.Builder(context, "default")


        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.logo) //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            val channelName = "코로나 예방 설문조사 알림"
            val description = "코로나 예방을위해 설문조사를 해주세요."
            val importance = NotificationManager.IMPORTANCE_HIGH //소리와 알림메시지를 같이 보여줌
            val channel = NotificationChannel("default", channelName, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        } else builder.setSmallIcon(R.mipmap.logo) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("자가검진")
                .setContentTitle("자가검진을 해주세요!")
                .setContentText("나이스 학생자가진단 설문조사를 해주세요. \n설정 -> 알람설정 끄기")
                .setStyle(NotificationCompat.BigTextStyle().bigText("나이스 학생자가진단 설문조사를 해주세요. \n설정 -> 알람설정 끄기"))
                .setContentIntent(pendingI)

        // 노티피케이션 동작시킴
        notificationManager.notify(1001, builder.build())
        val nextNotifyTime = Calendar.getInstance()
        nextNotifyTime.apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY,prefs.getInt("hourofday", 0))
            set(Calendar.MINUTE, prefs.getInt("minute", 0))
            set(Calendar.SECOND, 0)
        }

        // 내일 같은 시간으로 알람시간 결정
        nextNotifyTime.add(Calendar.DATE, 1)
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong("nextNotifyTime", nextNotifyTime.timeInMillis)
        editor.apply()
    }
}