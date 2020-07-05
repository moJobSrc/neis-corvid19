package com.lepitar.corvid19

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_teacher.*

class TeacherActivity : AppCompatActivity() {
    var result = ""
    var qstnCrtfcNoEncpt = ""
    lateinit var prefs :SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        //쿠키 싱크
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this)
        }
        webview.loadUrl("${prefs.getString("website", "http://")!!}/stv_cvd_co00_011.do?qstnCrtfcNoEncpt=&pName=${prefs.getString("name", "")!!}&qstnCrtfcNo=${prefs.getString("sms_key", "")}")
        webview.webViewClient = object : WebViewClient() {
            var sharedPreferences = getSharedPreferences("school", Context.MODE_PRIVATE)
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync()
                } else {
                    CookieManager.getInstance().flush()
                }
                finish()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("${prefs.getString("website", "http://")!!}/stv_cvd_co03_000.do?qstnCrtfcNoEncpt=${prefs.getString("k", "")!!}")
                view.context.startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync()
        }
    }

    override fun onBackPressed() {
        val customDialog = FinsihDialog(this)
        customDialog.setCancelable(true)
        customDialog.show()
    }
}