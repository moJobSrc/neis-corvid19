package com.lepitar.corvid19.SearchSchool;

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
import com.lepitar.corvid19.SmsActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.CustomViewHolder> {

    private ArrayList<SchoolData> schoolData = new ArrayList<>();

    public SchoolAdapter(ArrayList<SchoolData> schoolData) {
        this.schoolData = schoolData;
    }

    @NonNull
    @Override
    public SchoolAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schulname_list,parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SchoolAdapter.CustomViewHolder holder, int position) {
        holder.schoolname.setText(schoolData.get(position).getSchoolName());
        holder.schoolname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences("school", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String schoolName = holder.schoolname.getText().toString();
                editor.putString("schoolName", schoolName);
                editor.apply();
                context.startActivity(new Intent(v.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != schoolData ? schoolData.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView schoolname;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.schoolname = (TextView) itemView.findViewById(R.id.tv_schoolname);
        }
    }
}
