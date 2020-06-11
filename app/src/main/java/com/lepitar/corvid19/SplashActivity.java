package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    TextView progress;
    String result, schoolCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        result = "";
        progress = findViewById(R.id.progress);

        new CheckVersion().execute();

        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("autologin", false)) {
            new Confirm().execute();
        } else {
            Handler hd = new Handler();
            hd.postDelayed(new splashhandler(), 1500);
        }
    }

    private class splashhandler implements Runnable{
        public void run(){
            SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Intent verficate = new Intent(getApplicationContext(), Survey.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent teacher = new Intent(getApplicationContext(), TeacherActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent error = new Intent(getApplicationContext(), VerfyType.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //TCHER_SUCCESS
            if ("SUCCESS".equals(result)) {
                progress.setText("설문조사 페이지로 넘어갑니다..");
                startActivity(verficate);
                finish();
            } else if ("TCHER_SUCCESS".equals(result)) {
                Toast.makeText(SplashActivity.this, "선생님 계정으로 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(teacher);
                finish();
            }
            else if ("QSTN_USR_ERROR".equals(result)) {
                Toast.makeText(SplashActivity.this, "잘못된 본인확인 정보를 입력하였습니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                startActivity(error);
                finish();
                editor.putBoolean("autologin", false);
            }
            else if ("SCHOR_RFLT_YMD_ERROR".equals(result)) {
                Toast.makeText(SplashActivity.this, "학생 건강상태 자가진단 참여가능기간을 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                startActivity(error);
                finish();
                editor.putBoolean("autologin", false);
            }
            else {
                if (!"".equals(result)) {
                    Toast.makeText(SplashActivity.this, "잘못된 본인확인 정보입니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                startActivity(error);
                finish();
                editor.putBoolean("autologin", false);
            }
            editor.apply();
        }
    }

    private class CheckVersion extends AsyncTask<Void,Void,Void> {
        double version = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                version = Double.parseDouble(Jsoup.connect("https://play.google.com/store/apps/details?id=com.leeEunho.corvid19").timeout(5000).get()
                        .select("#fcxH9b > div.WpDbMd > c-wiz > div > div.ZfcPIb > div > div.JNury.Ekdcne > div > c-wiz:nth-child(4) > div.W4P4ne > div.JHTxhe.IQ1z0d > div > div:nth-child(4) > span > div").text());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void dummy) {
            super.onPostExecute(dummy);
            if (Double.parseDouble(BuildConfig.VERSION_NAME) < version)
                Toast.makeText(SplashActivity.this, "새로운 버전이 출시되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private class Confirm extends AsyncTask<Void,Void,Void> {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        String schoolName = sharedPreferences.getString("schoolName", "");
        String stdName = sharedPreferences.getString("name","");
        String bornDate = sharedPreferences.getString("birth","");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setText("학생정보 확인중입니다..");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (sharedPreferences.getBoolean("sms", false)) {
                    checkSms(sharedPreferences.getString("sms_key", ""),sharedPreferences.getString("name",""));
                } else {
                    getSchoolCode(schoolName);
                    checkAccount(schoolCode, schoolName, stdName, bornDate);
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 접속에 문제가 생겼습니다. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void dummy) {
            super.onPostExecute(dummy);
            Handler hd = new Handler();
            hd.postDelayed(new splashhandler(), 1500);
        }
    }

    public void checkAccount(String schoolCode, String schoolName, String stdName, String bornDate) throws JSONException, IOException {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        Document jsoup = Jsoup.connect(getSharedPreferences("school", MODE_PRIVATE).getString("website","")+"/stv_cvd_co00_012.do")
                .data("schulCode",schoolCode,"schulNm",schoolName,"pName",stdName,"frnoRidno",bornDate,"aditCrtfcNo",sharedPreferences.getString("overlap", ""))
                .method(Connection.Method.POST)
                .timeout(10000)
                .ignoreContentType(true).get();
        JSONObject jsonObject = (JSONObject) new JSONObject(jsoup.text()).get("resultSVO");
        result = jsonObject.getString("rtnRsltCode");
    }

    public void checkSms(String number, String pName) throws JSONException, IOException {
        Document jsoup = Jsoup.connect(getSharedPreferences("school", MODE_PRIVATE).getString("website","")+"/stv_cvd_co00_011.do")
                .data("qstnCrtfcNoEncpt","","pName",pName,"qstnCrtfcNo",number)
                .method(Connection.Method.POST)
                .timeout(10000)
                .ignoreContentType(true).get();
        JSONObject jsonObject = (JSONObject) new JSONObject(jsoup.text()).get("resultSVO");
        result = jsonObject.getString("rtnRsltCode");
    }

    public void getSchoolCode(String schoolName) throws Exception {
        Document jsoup = Jsoup.connect(getSharedPreferences("school", MODE_PRIVATE).getString("website","")+"/stv_cvd_co00_004.do")
                .data("schulNm",schoolName)
                .method(Connection.Method.POST)
                .timeout(10000)
                .ignoreContentType(true).get();
        JSONObject jsonObject = (JSONObject) new JSONObject(jsoup.text()).get("resultSVO");
        schoolCode = jsonObject.getString("schulCode");
    }

    @Override
    public void onBackPressed() {
        FinsihDialog customDialog = new FinsihDialog(this);
        customDialog.setCancelable(true);
        customDialog.show();
    }
}
