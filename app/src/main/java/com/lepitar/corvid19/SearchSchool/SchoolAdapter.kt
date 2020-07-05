package com.lepitar.corvid19.SearchSchool

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lepitar.corvid19.ui.MainActivity
import com.lepitar.corvid19.R
import java.util.*

class SchoolAdapter(schoolData: ArrayList<SchoolData>?) : RecyclerView.Adapter<SchoolAdapter.CustomViewHolder>() {
    private var schoolData: ArrayList<SchoolData>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schulname_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.schoolname.text = schoolData!![position].schoolName
        holder.list_item.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(v.context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("schulNm", schoolData!![position].schoolName)
            intent.putExtra("schulCode", schoolData!![position].schulCode)
            intent.putExtra("add", schoolData!![position].add)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return schoolData?.size ?: 0
    }

    inner class CustomViewHolder(itemView: View) : ViewHolder(itemView) {
        val schoolname = itemView.findViewById<View>(R.id.tv_schoolname) as TextView
        val list_item = itemView.findViewById<View>(R.id.list_item) as LinearLayout
    }

    init {
        this.schoolData = schoolData
    }
}