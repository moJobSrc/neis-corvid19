package com.lepitar.corvid19.ListAccount

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lepitar.corvid19.*
import com.lepitar.corvid19.ui.Survey
import com.lepitar.corvid19.ui.TeacherActivity
import java.util.*

class AccountAdapter(accountData: ArrayList<AccountData>?) : RecyclerView.Adapter<AccountAdapter.CustomViewHolder>() {
    private var accountData: ArrayList<AccountData>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_list, parent, false)
        return CustomViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (accountData!![position].sms) {
            holder.account_list.text = "${accountData!![position].name}(${accountData!![position].smskey})"
        } else if (accountData!![position].teacher) {
            holder.account_list.text = "${accountData!![position].name}(선생님)"
        } else {
            holder.account_list.text = "${accountData!![position].name}(${accountData!![position].birth})"
        }
        holder.account_list.setOnClickListener { v ->
            val context = v.context
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            if (accountData!![position].sms)  editor.putBoolean("sms", true) else editor.putBoolean("sms", false)

            editor.apply {
                putString("name", accountData!![position].name)
                putString("schoolName", accountData!![position].schoolName)
                putString("birth", accountData!![position].birth)
                putString("k", accountData!![position].k)
                putString("overlap", accountData!![position].overlap)
                putString("website", accountData!![position].website)
                putString("sms_key", accountData!![position].smskey)
                if (accountData!![position].teacher) putBoolean("teacher", true) else putBoolean("teacher", false)
                putBoolean("autologin", true)
                apply()
            }
            if (accountData!![position].teacher) {
                context.startActivity(Intent(v.context, TeacherActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            } else {
                context.startActivity(Intent(v.context, Survey::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
    }

    override fun getItemCount(): Int {
        return accountData?.size ?: 0
    }

    inner class CustomViewHolder(itemView: View) : ViewHolder(itemView) {
        var account_list: TextView = itemView.findViewById<View>(R.id.tv_account) as TextView
    }

    init {
        this.accountData = accountData
    }
}