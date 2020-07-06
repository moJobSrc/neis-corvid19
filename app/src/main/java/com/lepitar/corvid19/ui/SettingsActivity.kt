package com.lepitar.corvid19.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lepitar.corvid19.Alarm.AlarmReceiver
import com.lepitar.corvid19.Alarm.DeviceBootReceiver
import com.lepitar.corvid19.dialog.CustomDialog
import com.lepitar.corvid19.R
import kotlinx.android.synthetic.main.activity_universe_account.*
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.android.synthetic.main.settings_activity.adView
import kotlinx.android.synthetic.main.settings_activity.back
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        GlobalScope.launch {
            MobileAds.initialize(this@SettingsActivity) { }
            runOnUiThread {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }
        back.setOnClickListener { finish() }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        lateinit var alarm : SwitchPreference
        lateinit var account : Preference
        lateinit var student : Preference
        lateinit var prefs: SharedPreferences
        lateinit var edit: SharedPreferences.Editor

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            alarm = findPreference<SwitchPreference>("alarm")!!
            account = findPreference<Preference>("account")!!
            student = findPreference<Preference>("student")!!
            prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            edit = prefs.edit()
            val calendar = Calendar.getInstance()
            student.summary = prefs.getString("name", "학생 정보가 없습니다.")
            alarm.summary = prefs.getString("time", "시간")
            alarm.setOnPreferenceClickListener {
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    if (calendar.before(Calendar.getInstance())) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    edit.run {
                        putLong("nextNotifyTime", calendar.timeInMillis)
                        putString("time", SimpleDateFormat("a hh:mm").format(calendar.time))
                        putInt("hourofday", calendar.get(Calendar.HOUR_OF_DAY))
                        putInt("minute", calendar.get(Calendar.MINUTE))
                        apply()
                    }
                    diaryNotification(calendar, true)
                    alarm.summary = prefs.getString("time", "시간")
                },hour,minute,android.text.format.DateFormat.is24HourFormat(context))
                timePickerDialog.setOnCancelListener {
                    edit.putBoolean("alarm", false)
                    edit.remove("time").apply()
                    alarm.summary = prefs.getString("time", "시간")
                    alarm.isChecked = false
                }
                timePickerDialog.apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                }
                if (prefs.getBoolean("alarm", false)) timePickerDialog.show()
                else edit.remove("time").apply();alarm.summary = prefs.getString("time", "시간")
                false
            }
            account.setOnPreferenceClickListener {
                startActivity(Intent(context, UniverseAccount::class.java))
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                false
            }
            student.setOnPreferenceClickListener {
                lateinit var dialog: CustomDialog
                dialog = CustomDialog(requireContext(), "학생 정보를 초기화 하시겠습니까?", View.OnClickListener {
                    edit.apply {
                        remove("name")
                        remove("schoolName")
                        remove("birth")
                        remove("k")
                        remove("overlap")
                        remove("website")
                        remove("sms_key")
                        remove("teacher")
                        remove("autologin")
                        apply()
                    }
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), VerfyType::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                })
                dialog.show()
                false
            }
        }

        fun diaryNotification(calendar: Calendar, dailNotify: Boolean) {
            val pm: PackageManager = requireContext().getPackageManager()
            val receiver = ComponentName(requireContext(), DeviceBootReceiver::class.java)
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // 사용자가 매일 알람을 허용했다면
            if (dailNotify) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY, pendingIntent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
                // 부팅 후 실행되는 리시버 사용가능하게 설정
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP)
            } else { //Disable Daily Notifications
                if (PendingIntent.getBroadcast(requireContext(), 0, alarmIntent, 0) != null) {
                    alarmManager.cancel(pendingIntent)
                    //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
                }
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP)
            }
        }
    }
}