package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lepitar.corvid19.ListAccount.AccountAdapter;
import com.lepitar.corvid19.ListAccount.AccountData;
import com.lepitar.corvid19.SearchSchool.SchoolAdapter;
import com.lepitar.corvid19.SearchSchool.SchoolData;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UniverseAccount extends AppCompatActivity {

    private ArrayList<AccountData> arrayList = new ArrayList<>();
    RecyclerView account_list;
    AccountAdapter accountAdapter;
    LinearLayoutManager linearLayoutManager;
    ImageView back, add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universe_account);
        account_list = findViewById(R.id.account_list);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        linearLayoutManager = new LinearLayoutManager(this);
        account_list.setLayoutManager(linearLayoutManager);

        loadData();
        accountAdapter = new AccountAdapter(arrayList);
        account_list.setAdapter(accountAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("add",MODE_PRIVATE).edit();
                editor.putBoolean("add", true);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), VerfyType.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
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

}
