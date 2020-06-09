package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SmsActivity extends AppCompatActivity {
    Button next;
    EditText name, sms_key;
    ImageView back, setting;
    String result = "";
    String qstnCrtfcNoEncpt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);

        name = findViewById(R.id.name);
        sms_key = findViewById(R.id.sms_key);
        next = findViewById(R.id.confirm);
        back = findViewById(R.id.back);
        setting = findViewById(R.id.setting);

        name.setText(sharedPreferences.getString("name",""));
        sms_key.setText(sharedPreferences.getString("sms_key", ""));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Confirm().execute();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), settingActivity.class));
            }
        });
    }

    private class Confirm extends AsyncTask<Void,Void,Void> {
        String sms_number = sms_key.getText().toString();
        String stdName = name.getText().toString();
        ProgressDialog dialog = new ProgressDialog(SmsActivity.this);
        Boolean timeout = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.setContentView(R.layout.custom_progressbar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getResult(sms_number,stdName);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                timeout = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void dummy) {
            super.onPostExecute(dummy);
            dialog.dismiss();
            if (timeout) {
                Toast.makeText(SmsActivity.this, "서버가 문제가 있습니다 나중에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if ("SUCCESS".equals(result)) {
                    if (sharedPreferences.getBoolean("autologin", false)) {
                        Toast.makeText(SmsActivity.this, "자동으로 학생정보 확인됨", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SmsActivity.this, "학생정보가 저장되었습니다", Toast.LENGTH_SHORT).show();
                    }
                    editor.putString("name", name.getText().toString());
                    editor.putString("sms_key", sms_key.getText().toString());
                    editor.putString("k", qstnCrtfcNoEncpt);
                    editor.putBoolean("autologin", true);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), Survey.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if ("TCHER_SUCCESS".equals(result)) {
                    Toast.makeText(SmsActivity.this, "선생님 계정으로 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    editor.putString("name", name.getText().toString());
                    editor.putString("sms_key", sms_key.getText().toString());
                    editor.putString("k", qstnCrtfcNoEncpt);
                    editor.putBoolean("autologin", true);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), TeacherActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if ("QSTN_USR_ERROR".equals(result)) {
                    Toast.makeText(SmsActivity.this, "잘못된 본인확인 정보를 입력하였습니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("autologin", false);
                } else if ("SCHOR_RFLT_YMD_ERROR".equals(result)) {
                    Toast.makeText(SmsActivity.this, "학생 건강상태 자가진단 참여가능기간을 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("autologin", false);
                } else if (!result.isEmpty()){
                    Toast.makeText(SmsActivity.this, "잘못된 본인확인 정보입니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("autologin", false);
                } else {
                    Toast.makeText(SmsActivity.this, "잘못된 접속입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getResult(String number, String pName) throws JSONException, IOException {
        Document jsoup = Jsoup.connect(getSharedPreferences("school", MODE_PRIVATE).getString("website","")+"/stv_cvd_co00_011.do")
                .data("qstnCrtfcNoEncpt","","pName",pName,"qstnCrtfcNo",number)
                .method(Connection.Method.POST)
                .timeout(10000)
                .ignoreContentType(true).get();
        JSONObject jsonObject = (JSONObject) new JSONObject(jsoup.text()).get("resultSVO");
        result = jsonObject.getString("rtnRsltCode");
        qstnCrtfcNoEncpt = jsonObject.getString("qstnCrtfcNoEncpt");
    }


    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        FinsihDialog customDialog = new FinsihDialog(this);
        customDialog.setCancelable(true);
        customDialog.show();
    }
}
