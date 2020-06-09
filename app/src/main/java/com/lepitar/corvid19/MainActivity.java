package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lepitar.corvid19.ListAccount.AccountData;
import com.lepitar.corvid19.SearchSchool.SchoolData;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText schulNm, name, birth, add_info_edit;
    TextView add_info;
    Button search_school, confirm;
    String result, schoolCode,qstnCrtfcNoEncpt;
    ImageView back,setting;

    ArrayList<AccountData> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);

        schulNm = findViewById(R.id.schulNm);
        name = findViewById(R.id.name);
        birth = findViewById(R.id.birth);
        search_school = findViewById(R.id.search_school);
        confirm = findViewById(R.id.confirm);
        back = findViewById(R.id.back);
        setting = findViewById(R.id.setting);
        add_info = findViewById(R.id.add_info);
        add_info_edit = findViewById(R.id.add_info_edit);
        loadData();

        schulNm.setText(sharedPreferences.getString("schoolName",""));
        name.setText(sharedPreferences.getString("name",""));
        birth.setText(sharedPreferences.getString("birth",""));

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

        search_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SchulSearch.class));
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Confirm().execute();
            }
        });

        if (sharedPreferences.getBoolean("autologin", false) && !getSharedPreferences("add", MODE_PRIVATE).getBoolean("add", false)) new Confirm().execute();
    }

    private class Confirm extends AsyncTask<Void,Void,Void> {
        String schoolName = schulNm.getText().toString();
        String stdName = name.getText().toString();
        String bornDate = birth.getText().toString();
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.setContentView(R.layout.custom_progressbar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            result = "";
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getSchoolCode(schoolName);
                getResult(schoolCode,schoolName,stdName,bornDate);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                result = "error";
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 접속에 문제가 생겼습니다. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void dummy) {
            super.onPostExecute(dummy);
            dialog.dismiss();
            SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
            SharedPreferences add = getSharedPreferences("add", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if ("SUCCESS".equals(result)) {
                editor.putString("name", name.getText().toString());
                editor.putString("schoolName", schulNm.getText().toString());
                editor.putString("birth", birth.getText().toString());
                editor.putString("k", qstnCrtfcNoEncpt);
                editor.putString("overlap", add_info_edit.getText().toString());
                editor.putBoolean("autologin", true);
                editor.apply();
                if (add.getBoolean("add", false)) {
                    arrayList.add(new AccountData(name.getText().toString(), schulNm.getText().toString(), birth.getText().toString(), qstnCrtfcNoEncpt, add_info_edit.getText().toString(), getSharedPreferences("school", MODE_PRIVATE).getString("website", "")));
                    saveData();
                    startActivity(new Intent(getApplicationContext(), Survey.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    editor.putBoolean("add", false);
                } else {
                    if (sharedPreferences.getBoolean("autologin", false)) {
                        Toast.makeText(MainActivity.this, "자동으로 학생정보 확인됨", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "학생정보가 저장되었습니다", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(getApplicationContext(), Survey.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                arrayList.clear();
            } else if ("ADIT_CRTFC_NO".equals(result)) {
                Toast.makeText(MainActivity.this, "동일한 이름을 가진 학생이 있어, 추가정보를 입력해주시기 바랍니다.\n주민등록번호 마지막 2자리", Toast.LENGTH_SHORT).show();
                add_info_edit.setVisibility(View.VISIBLE);
                add_info.setVisibility(View.VISIBLE);
            } else if ("QSTN_USR_ERROR".equals(result)) {
                Toast.makeText(MainActivity.this, "잘못된 본인확인 정보를 입력하였습니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                editor.putBoolean("autologin", false);
            } else if ("SCHOR_RFLT_YMD_ERROR".equals(result)) {
                Toast.makeText(MainActivity.this, "학생 건강상태 자가진단 참여가능기간을 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                editor.putBoolean("autologin", false);
            } else if ("error".equals(result)) {
                editor.putBoolean("autologin", false);
            } else {
                Toast.makeText(MainActivity.this, "잘못된 본인확인 정보입니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                editor.putBoolean("autologin", false);
            }
        }
    }

    public void getResult(String schoolCode, String schoolName, String stdName, String bornDate) throws JSONException, IOException {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        Document jsoup = Jsoup.connect(getSharedPreferences("school", MODE_PRIVATE).getString("website","")+"/stv_cvd_co00_012.do")
                .data("schulCode",schoolCode,"schulNm",schoolName,"pName",stdName,"frnoRidno",bornDate,"aditCrtfcNo",sharedPreferences.getString("overlap", ""))
                .method(Connection.Method.POST)
                .timeout(10000)
                .ignoreContentType(true).get();
        JSONObject jsonObject = (JSONObject) new JSONObject(jsoup.text()).get("resultSVO");
        result = jsonObject.getString("rtnRsltCode");
        qstnCrtfcNoEncpt = jsonObject.getString("qstnCrtfcNoEncpt");
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

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("school", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("account_list", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("account_list", null);
        Type type = new TypeToken<ArrayList<AccountData>>() {}.getType();
        arrayList = gson.fromJson(json, type);

        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
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
