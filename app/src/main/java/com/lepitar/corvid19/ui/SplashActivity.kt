package com.lepitar.corvid19.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.lepitar.corvid19.BuildConfig
import com.lepitar.corvid19.dialog.FinsihDialog
import com.lepitar.corvid19.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class SplashActivity : AppCompatActivity() {
    var result = ""
    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()
        checkVersion()
        Handler().postDelayed(handler(), 800)
    }

    private inner class handler : Runnable {
        override fun run() {
            if (!prefs.getString("k", "").equals("") && prefs.getBoolean("teacher", false)) startActivity(Intent(this@SplashActivity, TeacherActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            else if (!prefs.getString("k", "").equals("") && !prefs.getBoolean("teacher", false)) startActivity(Intent(this@SplashActivity, Survey::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            else startActivity(Intent(this@SplashActivity, VerfyType::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun checkVersion() {
        var version = 0.0
        GlobalScope.launch {
            val loadingJob = async(Dispatchers.IO) {
                try {
                    version = Jsoup.connect("https://play.google.com/store/apps/details?id=com.leeEunho.corvid19").timeout(5000).get()
                            .select("#fcxH9b > div.WpDbMd > c-wiz > div > div.ZfcPIb > div > div.JNury.Ekdcne > div > c-wiz:nth-child(4) > div.W4P4ne > div.JHTxhe.IQ1z0d > div > div:nth-child(4) > span > div").text().toDouble()
                } catch (e : java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            loadingJob.join()
            if (BuildConfig.VERSION_NAME.toDouble() < version) toast("새로운 버전이 출시되었습니다.")
        }
    }

    private fun toast(argument : String) {
        runOnUiThread { Toast.makeText(this@SplashActivity, argument, Toast.LENGTH_SHORT).show() }
    }

    override fun onBackPressed() {
        val customDialog = FinsihDialog(this)
        customDialog.show()
    }
}