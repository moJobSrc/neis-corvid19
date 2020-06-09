package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class VerfyType extends AppCompatActivity {
    Button sms, account;
    ImageView setting, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfy_type);
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        SharedPreferences add = getSharedPreferences("add", MODE_PRIVATE);
        final SharedPreferences.Editor editor = getSharedPreferences("school", MODE_PRIVATE).edit();

        sms = findViewById(R.id.sms);
        account = findViewById(R.id.account);
        setting = findViewById(R.id.setting);
        back = findViewById(R.id.back);

        if (add.getBoolean("add", false)) {
            back.setVisibility(View.VISIBLE);
        }

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AreaActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                editor.putBoolean("account", true);
                editor.putBoolean("sms", false);
                editor.apply();
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AreaActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                editor.putBoolean("account", false);
                editor.putBoolean("sms", true);
                editor.apply();
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

    @Override
    public void onBackPressed() {
        FinsihDialog customDialog = new FinsihDialog(this);
        customDialog.setCancelable(true);
        customDialog.show();
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
