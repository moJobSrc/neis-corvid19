package com.lepitar.corvid19.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lepitar.corvid19.dialog.FinsihDialog
import com.lepitar.corvid19.ListAccount.AccountData
import com.lepitar.corvid19.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*

class MainActivity : AppCompatActivity() {

    var arrayList: ArrayList<AccountData>? = null
    var loadingJob : Job? = null
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var add : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()
        add = intent.getBooleanExtra("add", false)
        loadData()
        schulNm.setText(intent.getStringExtra("schulNm"))
        back.setOnClickListener{ finish() }
        setting.setOnClickListener{ startActivity(Intent(applicationContext, SettingsActivity::class.java)) }
        search_school.setOnClickListener{ startActivity(Intent(applicationContext, SchulSearch::class.java).putExtra("add", add!!)) }
        confirm.setOnClickListener{ confirm() }
    }

    private fun confirm() {
        val schulNm = schulNm.text.toString()
        val pName = name.text.toString()
        val frnoRidno = birth.text.toString()
        val overlap = add_info_edit.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            var result : String = ""
            var qstnCrtfcNoEncpt : String = ""

            loadingJob = async(Dispatchers.IO) {
                try {
                    val doc = Jsoup.connect(sharedPreferences.getString("website", "") + "/stv_cvd_co00_012.do")
                            .data("schulCode", intent.getStringExtra("schulCode")?: throw IllegalArgumentException("잘못된 본인확인 정보를 입력하였습니다.\n" +
                                    "확인 후 다시 시도해 주시기 바랍니다."), "schulNm", schulNm, "pName", pName, "frnoRidno", frnoRidno, "aditCrtfcNo", overlap)
                            .method(Connection.Method.POST)
                            .timeout(10000)
                            .ignoreContentType(true).get()
                    val jsonObject = JSONObject(doc.text()).getJSONObject("resultSVO")
                    result = jsonObject.getString("rtnRsltCode")
                    qstnCrtfcNoEncpt = jsonObject.getString("qstnCrtfcNoEncpt")
                } catch (e : Exception) {
                    toast(e.message!!)
                }
            }
            loadingJob!!.join()
            editor.putBoolean("autologin", false)
            when (result) {
                "SUCCESS" -> {
                    editor.apply {
                        putString("name", pName)
                        putString("schoolName", schulNm)
                        putString("birth", frnoRidno)
                        putString("k", qstnCrtfcNoEncpt)
                        putString("overlap", overlap)
                        putBoolean("teacher", false)
                        putBoolean("autologin", true)
                    }
                    if (add!!) {
                        arrayList!!.add(AccountData(pName,schulNm,frnoRidno,qstnCrtfcNoEncpt,overlap,"", sharedPreferences.getString("website", "")!!,false, false))
                        saveData()
                    }
                    startActivity(Intent(applicationContext, Survey::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                "ADIT_CRTFC_NO" -> {
                    toast("동일한 이름을 가진 학생이 있어, 추가정보를 입력해주시기 바랍니다.\n주민등록번호 마지막 2자리")
                    add_info.visibility = View.VISIBLE; add_info_edit.visibility = View.VISIBLE
                }
                "QSTN_USR_ERROR" -> {
                    toast("잘못된 본인확인 정보를 입력하였습니다.\n확인 후 다시 시도해 주시기 바랍니다.")
                }
                "SCHOR_RFLT_YMD_ERROR" -> {
                    toast("학생 건강상태 자가진단 참여가능기간을 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.")
                }
            }
            editor.apply()
        }
    }

    fun toast(argument : String) {
        runOnUiThread { Toast.makeText(applicationContext, argument, Toast.LENGTH_SHORT).show() }
    }

    fun saveData() {
        val json = Gson().toJson(arrayList)
        editor.putString("account_list", json)
        editor.apply()
    }

    fun loadData() {
        val json = sharedPreferences.getString("account_list", null)
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