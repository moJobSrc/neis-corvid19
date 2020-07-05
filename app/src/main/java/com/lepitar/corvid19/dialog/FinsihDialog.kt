package com.lepitar.corvid19.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lepitar.corvid19.R
import kotlinx.android.synthetic.main.activity_universe_account.*
import kotlinx.android.synthetic.main.finish_dialog.*
import kotlinx.android.synthetic.main.finish_dialog.adView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class FinsihDialog(context: Context) : Dialog(context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finish_dialog)
        GlobalScope.launch {
            MobileAds.initialize(context) { }
            ownerActivity?.runOnUiThread {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }
        btn_cancel!!.setOnClickListener(this)
        btn_ok!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_ok -> {
                android.os.Process.killProcess(android.os.Process.myPid())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ownerActivity?.finishAndRemoveTask()
                }
                ownerActivity?.finishAffinity()
                ownerActivity?.moveTaskToBack(true);
                exitProcess(1)
            }
        }
    }

}