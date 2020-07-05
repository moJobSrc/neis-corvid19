package com.lepitar.corvid19

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.finish_dialog.*
import kotlin.system.exitProcess

class FinsihDialog(context: Context) : Dialog(context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finish_dialog)
        MobileAds.initialize(context) { }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        btn_cancel!!.setOnClickListener(this)
        btn_ok!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_ok -> {
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