package com.lepitar.corvid19.Alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import java.util.*

class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (sharedPreferences.getBoolean("checked", false)) {
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
                val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().timeInMillis)
                val current_calendar = Calendar.getInstance()
                val nextNotifyTime: Calendar = GregorianCalendar()

                nextNotifyTime.timeInMillis = sharedPreferences.getLong("nextNotifyTime", millis)
                if (current_calendar.after(nextNotifyTime)) {
                    nextNotifyTime.add(Calendar.DATE, 1)
                }
                manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.timeInMillis,
                        AlarmManager.INTERVAL_DAY, pendingIntent)
            }
        }
    }
}