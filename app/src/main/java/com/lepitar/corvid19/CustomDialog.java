package com.lepitar.corvid19;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomDialog extends Dialog implements View.OnClickListener {
    String alarm_text;

    private TextView btn_cancel;
    private TextView btn_ok;
    private TextView text_alarm;

    private View.OnClickListener mLeftClickListener;

    public CustomDialog(@NonNull Context context, String alarm_text, View.OnClickListener btnok) {
        super(context);
        this.alarm_text = alarm_text;
        this.mLeftClickListener = btnok;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customdialog);

        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        text_alarm = findViewById(R.id.text_dialog);

        text_alarm.setText(alarm_text);
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(mLeftClickListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}