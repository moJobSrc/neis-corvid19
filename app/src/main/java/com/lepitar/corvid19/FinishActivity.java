package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FinishActivity extends AppCompatActivity {
    FinsihDialog customDialog;
    String check01,check02,check03,check04,check05,check07,check08,check09,check11,check13,check14,check15,qstnCrtfcNoEncpt,schulNm,stdntName,
            finish, corvidCheck,waring_tx = "";

    TextView tv_finish,tv_corvidCheck, warning;
    ImageView setting;

    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        tv_finish = findViewById(R.id.finish);
        tv_corvidCheck = findViewById(R.id.corvidCheck);
        warning = findViewById(R.id.warning);
        done = findViewById(R.id.done);
        setting = findViewById(R.id.setting);
        customDialog = new FinsihDialog(FinishActivity.this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.setCancelable(true);
                customDialog.show();
            }
        });

        Intent intent = getIntent();

        check01 = intent.getExtras().getString("check01");
        check02 = intent.getExtras().getString("check02");
        check03 = intent.getExtras().getString("check03");
        check04 = intent.getExtras().getString("check04");
        check05 = intent.getExtras().getString("check05");
        check07 = intent.getExtras().getString("check07");
        check08 = intent.getExtras().getString("check08");
        check09 = intent.getExtras().getString("check09");
        check11 = intent.getExtras().getString("check11");
        check13 = intent.getExtras().getString("check13");
        check14 = intent.getExtras().getString("check14");
        check15 = intent.getExtras().getString("check15");
        qstnCrtfcNoEncpt = intent.getExtras().getString("k");
        schulNm = intent.getExtras().getString("schulNm");
        stdntName = intent.getExtras().getString("stdntName");

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), settingActivity.class));
            }
        });

        new Confirm().execute();
    }

    private class Confirm extends AsyncTask<Void,Void,Void> {
        ProgressDialog dialog = new ProgressDialog(FinishActivity.this);

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
                Document doc = Jsoup.connect(getSharedPreferences("school", MODE_PRIVATE).getString("website","")+"/stv_cvd_co02_000.do")
                        .data("schulNm",schulNm,"stdntName",stdntName,"rtnRsltCode","SUCCESS","qstnCrtfcNoEncpt",qstnCrtfcNoEncpt
                                ,"rspns01",check01,"rspns02",check02,"rspns03",check03,"rspns04",check04,"rspns05",check05,"rspns07",check07
                                ,"rspns08",check08,"rspns09",check09,"rspns11",check11,"rspns13",check13,"rspns14",check14,"rspns15",check15)
                        .timeout(10000).get();
                finish = doc.select("#container > div > div > div > div:nth-child(4) > p").html().replace("&nbsp;", " ").replace("<br>","\n");
                if (doc.select("div.point2_wrap p.point2:not(:first-child)").text().isEmpty()) {
                    corvidCheck = doc.select("div.point2_wrap p.point2").text();
                } else {
                    waring_tx = doc.select("div.point2_wrap p.point2:not(:last-child)").text();
                    corvidCheck = doc.select("div.point2_wrap p.point2:not(:first-child)").text();
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 접속에 문제가 생겼습니다. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void dummy) {
            super.onPostExecute(dummy);
            dialog.dismiss();
            if (!waring_tx.isEmpty()) {
                warning.setVisibility(View.VISIBLE);
                warning.setText(waring_tx);
            } else {
                warning.setVisibility(View.GONE);
            }
            tv_finish.setText(finish);
            tv_corvidCheck.setText(corvidCheck);
        }
    }

    @Override
    public void onBackPressed() {
        customDialog.setCancelable(true);
        customDialog.show();
    }
}
