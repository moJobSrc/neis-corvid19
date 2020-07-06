package com.lepitar.corvid19.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lepitar.corvid19.dialog.CustomDialog
import com.lepitar.corvid19.ListAccount.AccountAdapter
import com.lepitar.corvid19.ListAccount.AccountData
import com.lepitar.corvid19.R
import kotlinx.android.synthetic.main.activity_universe_account.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class UniverseAccount : AppCompatActivity() {
    private var arrayList: ArrayList<AccountData>? = ArrayList()
    var accountAdapter: AccountAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var customDialog: CustomDialog? = null
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_universe_account)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()
        loadData()

        GlobalScope.launch {
            MobileAds.initialize(this@UniverseAccount) { }
            runOnUiThread {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }

        linearLayoutManager = LinearLayoutManager(this)
        accountAdapter = AccountAdapter(arrayList)
        account_list.layoutManager = linearLayoutManager
        account_list.adapter = accountAdapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(account_list)
        back.setOnClickListener(View.OnClickListener { finish() })
        add.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, VerfyType::class.java). apply {
                putExtra("add", true)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }

    private var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            try {
                val position = viewHolder.adapterPosition
                customDialog = CustomDialog(this@UniverseAccount, "해당 학생정보를 지우시겠습니까?", View.OnClickListener {
                    arrayList!!.removeAt(position)
                    accountAdapter!!.notifyItemRemoved(position)
                    saveData()
                    customDialog!!.dismiss()
                })
                customDialog!!.show()
                loadData()
                accountAdapter = AccountAdapter(arrayList)
                account_list.adapter = accountAdapter
            } catch (e: Exception) {
                Toast.makeText(this@UniverseAccount, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveData() {
        val json = Gson().toJson(arrayList)
        editor.putString("account_list", json)
        editor.apply()
    }

    fun loadData() {
        val json = sharedPreferences.getString("account_list", "")
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
}