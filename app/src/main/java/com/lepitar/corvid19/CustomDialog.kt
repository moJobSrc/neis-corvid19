package com.lepitar.corvid19

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.customdialog.*

class CustomDialog(context: Context, private val alarm_text: String, private val mLeftClickListener: View.OnClickListener) : Dialog(context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customdialog)

        text_dialog!!.text = alarm_text
        btn_cancel!!.setOnClickListener(this)
        btn_ok!!.setOnClickListener(mLeftClickListener)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> dismiss()
        }
    }

}