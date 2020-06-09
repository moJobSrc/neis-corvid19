package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class AreaActivity extends AppCompatActivity {
    /* 서울 */ //SEOUL("sen.go.kr"),
    /* 인천 */ //INCHEON("ice.go.kr"),
    /* 부산 */ //BUSAN("pen.go.kr"),
    /* 광주 */ //GWANGJU("gen.go.kr"),
    /* 대전 */ //DAEJEON("dje.go.kr"),
    /* 대구 */ //DAEGU("dge.go.kr"),
    /* 세종 */// SEJONG("sje.go.kr"),
    /* 울산 */ //ULSAN("use.go.kr"),
    /* 경기 */ //GYEONGGI("goe.go.kr"),
    /* 강원 */ //KANGWON("kwe.go.kr"),
    /* 충북 */ //CHUNGBUK("cbe.go.kr"),
    /* 충남 */ //CHUNGNAM("cne.go.kr"),
    /* 경북 */ //GYEONGBUK("gbe.go.kr"),
    /* 경남 */ //GYEONGNAM("gne.go.kr"),
    /* 전북 */ //JEONBUK("jbe.go.kr"),
    /* 전남 */ //JEONNAM("jne.go.kr"),
    /* 제주 */ //JEJU("jje.go.kr")

    RadioButton sen,ice,pen,gen,dje,dge,sje,use,goe,kwe,cbe,cne,gbe,gne,jbe,jne,jje;

    Button next;
    ImageView setting, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        setting = findViewById(R.id.setting);

        sen = findViewById(R.id.sen);
        ice = findViewById(R.id.ice);
        pen = findViewById(R.id.pen);
        gen = findViewById(R.id.gen);
        dje = findViewById(R.id.dje);
        dge = findViewById(R.id.dge);
        sje = findViewById(R.id.sje);
        use = findViewById(R.id.use);
        goe = findViewById(R.id.goe);
        kwe = findViewById(R.id.kwe);
        cbe = findViewById(R.id.cbe);
        cne = findViewById(R.id.cne);
        gbe = findViewById(R.id.gbe);
        gne = findViewById(R.id.gne);
        jbe = findViewById(R.id.jbe);
        jne = findViewById(R.id.jne);
        jje = findViewById(R.id.jje);
        final SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkArea();
                if (sharedPreferences.getBoolean("sms", false)) {
                    startActivity(new Intent(getApplicationContext(), SmsActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), settingActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void checkArea() {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sen.isChecked()) {
            editor.putString("website","https://eduro.sen.go.kr");
        }
        if (ice.isChecked()) {
            editor.putString("website","https://eduro.ice.go.kr");
        }
        if (pen.isChecked()) {
            editor.putString("website","https://eduro.pen.go.kr");
        }
        if (gen.isChecked()) {
            editor.putString("website","https://eduro.gen.go.kr");
        }
        if (dje.isChecked()) {
            editor.putString("website","https://eduro.dje.go.kr");
        }
        if (dge.isChecked()) {
            editor.putString("website","https://eduro.dge.go.kr");
        }
        if (sje.isChecked()) {
            editor.putString("website","https://eduro.sje.go.kr");
        }
        if (use.isChecked()) {
            editor.putString("website","https://eduro.use.go.kr");
        }
        if (goe.isChecked()) {
            editor.putString("website","https://eduro.goe.go.kr");
        }
        if (kwe.isChecked()) {
            editor.putString("website","https://eduro.kwe.go.kr");
        }
        if (cbe.isChecked()) {
            editor.putString("website","https://eduro.cbe.go.kr");
        }
        if (cne.isChecked()) {
            editor.putString("website","https://eduro.cne.go.kr");
        }
        if (gbe.isChecked()) {
            editor.putString("website","https://eduro.gbe.go.kr");
        }
        if (gne.isChecked()) {
            editor.putString("website","https://eduro.gne.go.kr");
        }
        if (jbe.isChecked()) {
            editor.putString("website","https://eduro.jbe.go.kr");
        }
        if (jne.isChecked()) {
            editor.putString("website","https://eduro.jne.go.kr");
        }
        if (jje.isChecked()) {
            editor.putString("website","https://eduro.jje.go.kr");
        }
        editor.apply();
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
