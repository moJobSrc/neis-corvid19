package com.lepitar.corvid19

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_verfy_type.*

class VerfyType : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    var add : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verfy_type)

        add = intent.getBooleanExtra("add", false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (add!!) back.visibility = View.VISIBLE
        account.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, AreaActivity::class.java).putExtra("add", add!!))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        sms.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, AreaActivity::class.java).putExtra("add", add!!).putExtra("sms", true))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        setting.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, SettingsActivity::class.java)) })
        back.setOnClickListener(View.OnClickListener { finish() })
    }

    override fun onBackPressed() {
        val customDialog = FinsihDialog(this)
        customDialog.show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}