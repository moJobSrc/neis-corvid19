package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lepitar.corvid19.SearchSchool.SchoolAdapter;
import com.lepitar.corvid19.SearchSchool.SchoolData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SchulSearch extends AppCompatActivity {

    private ArrayList<SchoolData> arrayList = new ArrayList<>();
    private ArrayList<SchoolData> newList = new ArrayList<>();
    private RecyclerView schoolname;
    private SchoolAdapter schoolAdapter;
    private LinearLayoutManager linearLayoutManager;
    EditText schulNm;
    Button search_Button;
    String url = "";
    ImageView back,setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schulsearch);

        url = getSharedPreferences("school", MODE_PRIVATE).getString("website","");

        schulNm = findViewById(R.id.schulNm);
        search_Button = findViewById(R.id.search_school);
        back = findViewById(R.id.back);
        setting = findViewById(R.id.setting);

        schoolname = findViewById(R.id.school_name);
        linearLayoutManager = new LinearLayoutManager(this);
        schoolname.setLayoutManager(linearLayoutManager);

        search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Test().execute();
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

    private class Test extends AsyncTask<Void,Void,Void> {
        String schoolName = schulNm.getText().toString();
        ProgressDialog dialog = new ProgressDialog(SchulSearch.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.dismiss();
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
                newList.clear();
                Document doc = Jsoup.connect(url+"/stv_cvd_co00_003.do").data("schulNm",schoolName).timeout(10000).get();
                Elements ele = doc.select("tr td a");
                for(Element element : ele) {
                    newList.add(new SchoolData(element.text()));
                }
            } catch (IOException e) {
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
            arrayList.clear();
            arrayList.addAll(newList);
            schoolAdapter = new SchoolAdapter(arrayList);
            schoolname.setAdapter(schoolAdapter);
        }
    }
}
