package com.lepitar.corvid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
    AdView mAdView;
    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universe_account);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        account_list = findViewById(R.id.account_list);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        linearLayoutManager = new LinearLayoutManager(this);
        account_list.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(account_list);

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

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                final int position = viewHolder.getAdapterPosition();
                View.OnClickListener btnok = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.remove(position);
                        accountAdapter.notifyItemRemoved(position);
                        saveData();
                        customDialog.dismiss();
                    }
                };

                customDialog = new CustomDialog(UniverseAccount.this, "해당 학생정보를 지우시겠습니까?", btnok);
                customDialog.setCancelable(false);
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.show();
                loadData();
                accountAdapter = new AccountAdapter(arrayList);
                account_list.setAdapter(accountAdapter);
            } catch (Exception e) {
                Toast.makeText(UniverseAccount.this, e.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences("account", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("account_list", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("account_list", "");
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
