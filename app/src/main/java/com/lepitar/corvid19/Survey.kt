package com.lepitar.corvid19

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_survey.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class Survey : AppCompatActivity() {
    var customDialog: CustomDialog? = null
    var result = ""
    var schulNm = ""
    var stdntName= ""
    lateinit var url : String
    lateinit var qstnCrtfcNoEncpt : String
    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var text_1 = "1. 학생의 몸에 열이 있나요 ? (해당사항 선택)\n코로나19와 관계없이 평소에 발열 증상이 계속되는 경우는 제외"
    var text_2 = "2. 학생에게 코로나19가 의심되는 증상이 있나요 ? (모두 선택)\n단, 코로나19와 관계없이 평소에 증상이 계속되는 경우는 제외"
    var text_3 = "3. 학생이 최근(14일 이내) 해외여행을 다녀온 사실이 있나요 ?"
    var text_4 = "4. 동거가족 중 14일 이내 해외여행을 다녀온 사실이 있나요 ? \n(직업특성상 매번 해외 입출국하고 의심증상이 없는 경우는 제외)"
    var text_5 = "5. 동거가족 중 현재 자가격리 중 인 가족이 있나요 ?"
    var check01: String = ""
    var check02: String = ""
    var check03: String = ""
    var check04: String = ""
    var check05: String = ""
    var check07: String = ""
    var check08: String = ""
    var check09: String = ""
    var check11: String = ""
    var check13: String = ""
    var check14: String = ""
    var check15: String = ""

    /*
    2,3,4,5,6,10,11

    발열 증상 : rspns01 value : 37.5 미만 _2, 37.5 ~ 38 미만또는 발열감 2, 38도 이상 _3

    코로나 19 의심증상 : rspns02 아니오, 그룹(rspns03 기침,rspns04 인후통,rspns05 호흡곤란,rspns06 설사,rspns10 메스꺼움,rspns11 미각후각마비)

    학생 해외여행 : rspns07 value: 아니오, 예 _1

    동거가족 해외여행 : rspns08 value 아니오 0 예 _1

    동거가족 자가격리 : rspns09 value 아니오 0 예 _1

    (2번 코로나 19의심은 모든값이 1로 리턴됨)

    모두아니오
    1번 rspns01 1
    2번 rspns02 0
    3번 rspns07 0
    4번 rspns08 0
    5번 rspns09 0
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        //색상변경
        setColorText()
        //체크박스 리스터
        setCheckBoxListener()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()
        url = prefs.getString("website", "")!!
        confirm.setOnClickListener {
            if (rspns02!!.isChecked || rspns03!!.isChecked || rspns04!!.isChecked
                    || rspns05!!.isChecked || rspns13!!.isChecked || rspns14!!.isChecked || rspns15!!.isChecked || rspns11!!.isChecked) {
                if (rspns01_2!!.isChecked || rspns03!!.isChecked || rspns04!!.isChecked
                        || rspns05!!.isChecked || rspns13!!.isChecked || rspns14!!.isChecked || rspns15!!.isChecked || rspns11!!.isChecked || rspns07_1!!.isChecked || rspns08_1!!.isChecked || rspns09_1!!.isChecked) {
                    customDialog = CustomDialog(this@Survey, "코로나19 유증상 항목을 선택/응답하였습니다.\n응답 내용을 제출하시겠습니까?", View.OnClickListener { checkBoxValue()
                        submit() })
                    customDialog!!.show()
                } else {
                    checkBoxValue()
                    submit()
                }
            } else {
                Toast.makeText(this@Survey, "2번째 문항 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
        setting.setOnClickListener { startActivity(Intent(applicationContext, SettingsActivity::class.java)) }
    }

    fun submit() {
        GlobalScope.launch {
            val loadingJob = async(Dispatchers.IO) {
                try {
                    qstnCrtfcNoEncpt = prefs.getString("k", "")!!
                    Log.d("test", url)
                    val doc = Jsoup.connect("$url/stv_cvd_co01_000.do")
                            .data("rtnRsltCode", "SUCCESS", "qstnCrtfcNoEncpt", qstnCrtfcNoEncpt
                                    , "schulNm", "", "stdntName", ""
                                    , "rspns01", check01, "rspns02", check02, "rspns03", check03, "rspns04", check04, "rspns05", check05
                                    , "rspns07", check07, "rspns08", check08, "rspns09", check09, "rspns11", check11, "rspns13", check13, "rspns14", check14, "rspns15", check15)
                            .timeout(10000).ignoreContentType(true).get()
                    val jsonObject = JSONObject(doc.text()).getJSONObject("resultSVO")
                    result = jsonObject.getString("rtnRsltCode")
                    schulNm = jsonObject.getString("schulNm")
                    stdntName = jsonObject.getString("stdntName")
                } catch (e : java.lang.Exception) {
                    e.printStackTrace()
                    runOnUiThread{Toast.makeText(applicationContext,R.string.error, Toast.LENGTH_SHORT).show()}
                }
            }
            loadingJob.join()
            if ("SUCCESS" == result) {
                val intent = Intent(applicationContext, FinishActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).apply {
                    putExtra("k", qstnCrtfcNoEncpt) //학생고유 인증주소
                    putExtra("schulNm", schulNm) //학교이름
                    putExtra("stdntName", stdntName) //학생이름
                    putExtra("check01", check01) //문제1
                    putExtra("check02", check02) //문제2
                    putExtra("check03", check03) //문제2
                    putExtra("check04", check04) //문제2
                    putExtra("check05", check05) //문제2
                    putExtra("check07", check07) //문제2
                    putExtra("check08", check08) //문제3
                    putExtra("check09", check09) //문제4
                    putExtra("check11", check11) //문제5
                    putExtra("check13", check13) //문제2
                    putExtra("check14", check14) //문제2
                    putExtra("check15", check15) //문제2
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            } else {
                runOnUiThread { Toast.makeText(this@Survey, "잘못된 본인확인 정보입니다.\n참여주소 또는 본인확인 정보를 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    fun setCheckBoxListener() {
        val Listener = View.OnClickListener { view ->
            when (view.id) {
                R.id.rspns03, R.id.rspns05, R.id.rspns13, R.id.rspns14, R.id.rspns04, R.id.rspns11, R.id.rspns15 -> rspns02!!.isChecked = false
                R.id.rspns02 -> {
                    rspns03!!.isChecked = false
                    rspns04!!.isChecked = false
                    rspns05!!.isChecked = false
                    rspns13!!.isChecked = false
                    rspns14!!.isChecked = false
                    rspns15!!.isChecked = false
                    rspns11!!.isChecked = false
                }
            }
        }
        rspns02!!.setOnClickListener(Listener)
        rspns03!!.setOnClickListener(Listener)
        rspns04!!.setOnClickListener(Listener)
        rspns05!!.setOnClickListener(Listener)
        rspns13!!.setOnClickListener(Listener)
        rspns14!!.setOnClickListener(Listener)
        rspns15!!.setOnClickListener(Listener)
        rspns11!!.setOnClickListener(Listener)
    }

    fun setColorText() {
        val tx_1 = SpannableStringBuilder(text_1)
        val tx_2 = SpannableStringBuilder(text_2)
        val tx_3 = SpannableStringBuilder(text_3)
        val tx_4 = SpannableStringBuilder(text_4)
        val tx_5 = SpannableStringBuilder(text_5)
        tx_1.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 19, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_1.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_2.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 30, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_2.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_3.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 18, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_3.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_4.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 17, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_4.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tx_5.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        prb1!!.text = tx_1
        prb2!!.text = tx_2
        prb3!!.text = tx_3
        prb4!!.text = tx_4
        prb5!!.text = tx_5
    }

    fun checkBoxValue() {
        if (rspns01.isChecked) { //문제1
            check01 = "1"
        }
        if (rspns01_2.isChecked) { //문제1 2번
            check01 = "2"
        }
        if (rspns02!!.isChecked) { //문제2
            check02 = "1"
        }
        if (rspns03.isChecked) { //문제2 3
            check02 = ""
            check03 = "1"
        }
        if (rspns04.isChecked) { //문제2 4
            check02 = ""
            check04 = "1"
        }
        if (rspns05.isChecked) { //문제2 5
            check02 = ""
            check05 = "1"
        }
        if (rspns13.isChecked) { //문제2 6
            check02 = ""
            check13 = "1"
        }
        if (rspns14.isChecked) { //문제2 7
            check02 = ""
            check14 = "1"
        }
        if (rspns15.isChecked) { //문제2 8
            check02 = ""
            check15 = "1"
        }
        if (rspns11.isChecked) { //문제2 8
            check02 = ""
            check11 = "1"
        }
        if (rspns07.isChecked) { //문제3
            check07 = "0"
        }
        if (rspns07_1.isChecked) { //문제3 아니오
            check07 = "1"
        }
        if (rspns08.isChecked) { //문제4 예
            check08 = "0"
        }
        if (rspns08_1.isChecked) { //문제4 아니오
            check08 = "1"
        }
        if (rspns09.isChecked) { //문제5 예
            check09 = "0"
        }
        if (rspns09_1.isChecked) { //문제5 아니오
            check09 = "1"
        }
    }

    override fun onStop() {
        super.onStop()
        if (customDialog != null) {
            customDialog!!.dismiss()
            customDialog = null
        }
    }

    override fun onBackPressed() {
        val customDialog = FinsihDialog(this)
        customDialog.setCancelable(true)
        customDialog.show()
    }
}