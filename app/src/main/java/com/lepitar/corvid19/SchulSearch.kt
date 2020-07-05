package com.lepitar.corvid19

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lepitar.corvid19.SearchSchool.SchoolAdapter
import com.lepitar.corvid19.SearchSchool.SchoolData
import kotlinx.android.synthetic.main.activity_schulsearch.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class SchulSearch : AppCompatActivity() {
    private val arrayList: ArrayList<SchoolData> = ArrayList<SchoolData>()
    private val newList: ArrayList<SchoolData> = ArrayList<SchoolData>()
    private lateinit var schoolAdapter: SchoolAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var url : String
    private var loadingJob : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schulsearch)
        url = PreferenceManager.getDefaultSharedPreferences(this).getString("website", "")!!
        linearLayoutManager = LinearLayoutManager(this)
        school_name.layoutManager = linearLayoutManager
        search_school.setOnClickListener{ search() }
        back.setOnClickListener{ finish() }
        setting.setOnClickListener{ startActivity(Intent(applicationContext, SettingsActivity::class.java)) }
    }

    fun search() {
        GlobalScope.launch {
            loadingJob = async(Dispatchers.IO) {
                try {
                    newList.clear()
                    val doc = Jsoup.connect("$url/stv_cvd_co00_003.do").data("schulNm", schulNm.text.toString()).timeout(10000).get()
                    for (element in doc.select("tr td a")) {
                        val schulCode = element.attr("onClick").replace("javscript:selectSchul(", "").replace(");", "").replace("'", "").split(",").toTypedArray()[0]
                        newList.add(SchoolData(element.text(), schulCode, intent.getBooleanExtra("add", false)))
                    }
                } catch (e : Exception) {
                    toast(applicationContext.getString(R.string.error))
                }
            }
            loadingJob!!.join()
            runOnUiThread {
                arrayList.clear()
                arrayList.addAll(newList)
                schoolAdapter = SchoolAdapter(arrayList)
                school_name!!.adapter = schoolAdapter
            }
        }
    }

    fun toast(argument : String) {
        runOnUiThread {
            Toast.makeText(this@SchulSearch, argument, Toast.LENGTH_SHORT).show()
        }
    }
}