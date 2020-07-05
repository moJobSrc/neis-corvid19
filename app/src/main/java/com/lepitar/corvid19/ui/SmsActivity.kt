package com.lepitar.corvid19.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lepitar.corvid19.dialog.FinsihDialog
import com.lepitar.corvid19.ListAccount.AccountData
import com.lepitar.corvid19.R
import kotlinx.android.synthetic.main.activity_sms.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.lang.Exception
import java.util.*

class SmsActivity : AppCompatActivity() {
    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var add : Boolean? = null
    var result = ""
    var qstnCrtfcNoEncpt = ""
    var arrayList: ArrayList<AccountData>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()
        add = intent.getBooleanExtra("add", false)
        loadData()
        name.setText(prefs.getString("name", ""))
        sms_key.setText(prefs.getString("sms_key", ""))
        confirm.setOnClickListener{ confirm() }
        back.setOnClickListener{ finish() }
        setting.setOnClickListener{ startActivity(Intent(applicationContext, SettingsActivity::class.java)) }
    }

    fun confirm() {
        GlobalScope.launch {
            val loadingJob = async(Dispatchers.IO) {
                try {
                    val jsoup = Jsoup.connect(prefs.getString("website", "") + "/stv_cvd_co00_011.do")
                            .data("qstnCrtfcNoEncpt", "", "pName", name.text.toString(), "qstnCrtfcNo", sms_key.text.toString())
                            .method(Connection.Method.POST)
                            .timeout(10000)
                            .ignoreContentType(true).get()
                    val jsonObject = JSONObject(jsoup.text()).getJSONObject("resultSVO")
                    result = jsonObject.getString("rtnRsltCode")
                    qstnCrtfcNoEncpt = jsonObject.getString("qstnCrtfcNoEncpt")
                } catch (e : Exception) {
                    runOnUiThread { Toast.makeText(this@SmsActivity, R.string.error, Toast.LENGTH_SHORT).show() }
                }
            }
            loadingJob.join()
            editor.putBoolean("autologin", false)
            when (result) {
                "SUCCESS" -> {
                    editor.apply {
                        putString("name", name.text.toString())
                        putString("sms_key", sms_key.text.toString())
                        putString("k", qstnCrtfcNoEncpt)
                        putBoolean("teacher", false)
                        putBoolean("autologin", true)
                    }
                    if (add!!) {
                        arrayList!!.add(AccountData(name.text.toString(), "", "", qstnCrtfcNoEncpt, "", sms_key.text.toString(), prefs.getString("website", "")!!, true, false))
                        saveData()
                    }
                    startActivity(Intent(this@SmsActivity, Survey::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                "TCHER_SUCCESS" -> {
                    editor.apply {
                        putString("name", name.text.toString())
                        putString("sms_key", sms_key.text.toString())
                        putString("k", qstnCrtfcNoEncpt)
                        putBoolean("teacher", true)
                        putBoolean("autologin", true)
                    }
                    if (add!!) {
                        arrayList!!.add(AccountData(name.text.toString(), "", "", qstnCrtfcNoEncpt, "", sms_key.text.toString(), prefs.getString("website", "")!!, true, true))
                        saveData()
                    }
                    startActivity(Intent(this@SmsActivity, TeacherActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                "QSTN_USR_ERROR" -> {
                    runOnUiThread{ Toast.makeText(this@SmsActivity, "잘못된 본인확인 정보를 입력하였습니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show() }
                }
                "SCHOR_RFLT_YMD_ERROR" -> {
                    runOnUiThread { Toast.makeText(this@SmsActivity, "학생 건강상태 자가진단 참여가능기간을 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show() }
                }
            }
            editor.apply()
        }
    }
    fun saveData() {
        val json = Gson().toJson(arrayList)
        editor.putString("account_list", json)
        editor.apply()
    }

    fun loadData() {
        val json = prefs.getString("account_list", null)
        val type = object : TypeToken<ArrayList<AccountData?>?>() {}.type
        arrayList = Gson().fromJson<ArrayList<AccountData>>(json, type)
        if (arrayList == null) {
            arrayList = ArrayList()
        }
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