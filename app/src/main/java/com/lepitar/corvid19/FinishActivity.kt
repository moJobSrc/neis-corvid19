package com.lepitar.corvid19

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_finish.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class FinishActivity : AppCompatActivity() {
    var customDialog: FinsihDialog? = null
    lateinit var check01: String
    lateinit var check02: String
    lateinit var check03: String
    lateinit var check04: String
    lateinit var check05: String
    lateinit var check07: String
    lateinit var check08: String
    lateinit var check09: String
    lateinit var check11: String
    lateinit var check13: String
    lateinit var check14: String
    lateinit var check15: String
    var qstnCrtfcNoEncpt: String? = null
    var schulNm: String? = null
    var stdntName: String? = null
    var finish: String? = null
    var covidCheck: String? = null
    var waring_tx = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)
        customDialog = FinsihDialog(this@FinishActivity)
        done.setOnClickListener(View.OnClickListener {
            customDialog!!.show()
        })
        check01 = intent.extras!!.getString("check01")!!
        check02 = intent.extras!!.getString("check02")!!
        check03 = intent.extras!!.getString("check03")!!
        check04 = intent.extras!!.getString("check04")!!
        check05 = intent.extras!!.getString("check05")!!
        check07 = intent.extras!!.getString("check07")!!
        check08 = intent.extras!!.getString("check08")!!
        check09 = intent.extras!!.getString("check09")!!
        check11 = intent.extras!!.getString("check11")!!
        check13 = intent.extras!!.getString("check13")!!
        check14 = intent.extras!!.getString("check14")!!
        check15 = intent.extras!!.getString("check15")!!
        qstnCrtfcNoEncpt = intent.extras!!.getString("k")
        schulNm = intent.extras!!.getString("schulNm")
        stdntName = intent.extras!!.getString("stdntName")
        setting.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, SettingsActivity::class.java)) })
        confirm()
    }

    fun confirm() {
        GlobalScope.launch {
            val loadingJob = async {
                val doc = Jsoup.connect(getSharedPreferences("school", Context.MODE_PRIVATE).getString("website", "") + "/stv_cvd_co02_000.do")
                        .data("schulNm", schulNm, "stdntName", stdntName, "rtnRsltCode", "SUCCESS", "qstnCrtfcNoEncpt", qstnCrtfcNoEncpt
                                , "rspns01", check01, "rspns02", check02, "rspns03", check03, "rspns04", check04, "rspns05", check05, "rspns07", check07
                                , "rspns08", check08, "rspns09", check09, "rspns11", check11, "rspns13", check13, "rspns14", check14, "rspns15", check15)
                        .timeout(10000).get()
                finish = doc.select("#container > div > div > div > div:nth-child(4) > p").html().replace("&nbsp;", " ").replace("<br>", "\n")
                if (doc.select("div.point2_wrap p.point2:not(:first-child)").text().isEmpty()) {
                    covidCheck = doc.select("div.point2_wrap p.point2").text()
                } else {
                    waring_tx = doc.select("div.point2_wrap p.point2:not(:last-child)").text()
                    covidCheck = doc.select("div.point2_wrap p.point2:not(:first-child)").text()
                }
            }
            loadingJob.join()
            if (waring_tx.isNotEmpty()) {
                warning!!.visibility = View.VISIBLE
                warning!!.text = waring_tx
            } else {
                warning!!.visibility = View.GONE
            }
            end.text = finish
            covid.text = covidCheck
        }
    }

    override fun onBackPressed() {
        customDialog!!.show()
    }
}