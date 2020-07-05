package com.lepitar.corvid19

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_area.*

class AreaActivity : AppCompatActivity() {
    /* 서울 */ //SEOUL("sen.go.kr"),
/* 인천 */ //INCHEON("ice.go.kr"),
/* 부산 */ //BUSAN("pen.go.kr"),
/* 광주 */ //GWANGJU("gen.go.kr"),
/* 대전 */ //DAEJEON("dje.go.kr"),
/* 대구 */ //DAEGU("dge.go.kr"),
/* 세종 */ // SEJONG("sje.go.kr"),
/* 울산 */ //ULSAN("use.go.kr"),
/* 경기 */ //GYEONGGI("goe.go.kr"),
/* 강원 */ //KANGWON("kwe.go.kr"),
/* 충북 */ //CHUNGBUK("cbe.go.kr"),
/* 충남 */ //CHUNGNAM("cne.go.kr"),
/* 경북 */ //GYEONGBUK("gbe.go.kr"),
/* 경남 */ //GYEONGNAM("gne.go.kr"),
/* 전북 */ //JEONBUK("jbe.go.kr"),
/* 전남 */ //JEONNAM("jne.go.kr"),
/* 제주 */ //JEJU("jje.go.kr")
    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var add : Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()
        add = intent.getBooleanExtra("add", false)
        next.setOnClickListener{
            checkArea()
            if (intent.getBooleanExtra("sms", false)) startActivity(Intent(applicationContext, SmsActivity::class.java).putExtra("add", add!!))
            else startActivity(Intent(applicationContext, MainActivity::class.java).putExtra("add", add!!))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        setting.setOnClickListener{ startActivity(Intent(applicationContext, SettingsActivity::class.java)) }
        back.setOnClickListener { finish() }
    }

    fun checkArea() {
        if (sen.isChecked)
            editor.putString("website", "https://eduro.sen.go.kr")
        else if (ice.isChecked)
            editor.putString("website", "https://eduro.ice.go.kr")
        else if (pen.isChecked)
            editor.putString("website", "https://eduro.pen.go.kr")
        else if (gen.isChecked)
            editor.putString("website", "https://eduro.gen.go.kr")
        else if (dje.isChecked)
            editor.putString("website", "https://eduro.dje.go.kr")
        else if (dge.isChecked)
            editor.putString("website", "https://eduro.dge.go.kr")
        else if (sje.isChecked)
            editor.putString("website", "https://eduro.sje.go.kr")
        else if (use.isChecked)
            editor.putString("website", "https://eduro.use.go.kr")
        else if (goe.isChecked)
            editor.putString("website", "https://eduro.goe.go.kr")
        else if (kwe.isChecked)
            editor.putString("website", "https://eduro.kwe.go.kr")
        else if (cbe.isChecked)
            editor.putString("website", "https://eduro.cbe.go.kr")
        else if (cne.isChecked)
            editor.putString("website", "https://eduro.cne.go.kr")
        else if (gbe.isChecked) //경북
            editor.putString("website", "https://eduro.gbe.kr")
        else if (gne.isChecked)
            editor.putString("website", "https://eduro.gne.go.kr")
        else if (jbe.isChecked)
            editor.putString("website", "https://eduro.jbe.go.kr")
        else if (jne.isChecked)
            editor.putString("website", "https://eduro.jne.go.kr")
        else if (jje.isChecked)
            editor.putString("website", "https://eduro.jje.go.kr")
        editor.apply()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        val customDialog = FinsihDialog(this)
        customDialog.setCancelable(true)
        customDialog.show()
    }
}