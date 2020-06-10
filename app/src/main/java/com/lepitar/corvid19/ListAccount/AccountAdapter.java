package com.lepitar.corvid19.ListAccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lepitar.corvid19.MainActivity;
import com.lepitar.corvid19.R;
import com.lepitar.corvid19.SearchSchool.SchoolData;
import com.lepitar.corvid19.SmsActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.CustomViewHolder> {

    private ArrayList<AccountData> accountData = new ArrayList<>();

    public AccountAdapter(ArrayList<AccountData> accountData) {
        this.accountData = accountData;
    }

    @NonNull
    @Override
    public AccountAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list,parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountAdapter.CustomViewHolder holder, final int position) {
        if (accountData.get(position).getBirth().isEmpty()) {
            holder.account_list.setText(accountData.get(position).getName() + "(" + accountData.get(position).getSmskey() + ")");
        } else {
            holder.account_list.setText(accountData.get(position).getName() + "(" + accountData.get(position).getBirth() + ")");
        }
        holder.account_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences("school", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                SharedPreferences.Editor add =  context.getSharedPreferences("add", MODE_PRIVATE).edit();
                if (accountData.get(position).getSms()) {
                    editor.putBoolean("sms", true);
                } else {
                    editor.putBoolean("sms", false);

                }
                editor.putString("name", accountData.get(position).getName());
                editor.putString("schoolName",accountData.get(position).getSchoolName());
                editor.putString("birth", accountData.get(position).getBirth());
                editor.putString("k", accountData.get(position).getK());
                editor.putString("overlap", accountData.get(position).getOverlap());
                editor.putString("website", accountData.get(position).getWebsite());
                editor.putString("sms_key", accountData.get(position).getSmskey());
                editor.putBoolean("autologin",true);
                add.putBoolean("add", false);
                add.apply();
                editor.apply();
                if (accountData.get(position).getSms()) {
                    context.startActivity(new Intent(v.getContext(), SmsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    context.startActivity(new Intent(v.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != accountData ? accountData.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView account_list;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.account_list = (TextView) itemView.findViewById(R.id.tv_account);
        }
    }
}
