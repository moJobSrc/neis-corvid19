package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.lepitar.corvid19.Alarm.AlarmReceiver;
import com.lepitar.corvid19.Alarm.DeviceBootReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class settingActivity extends AppCompatActivity {

    Context mContext = this;

    TextView tv_alarm,name,school,birth;
    LinearLayout reset;
    Switch alarm;
    ImageView back;
    AdView mAdView;
    LinearLayout universe_account;

    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        universe_account = findViewById(R.id.universe_account);
        tv_alarm = findViewById(R.id.alarm_time);
        reset = findViewById(R.id.reset);
        back = findViewById(R.id.back);
        name = findViewById(R.id.name);
        school = findViewById(R.id.school);
        birth = findViewById(R.id.birth);
        alarm = findViewById(R.id.alarm);

        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);

        school.setText(sharedPreferences.getString("schoolName","학생정보가 없습니다.") );
        name.setText(sharedPreferences.getString("name",""));
        birth.setText(sharedPreferences.getString("birth",""));
        tv_alarm.setText(getSharedPreferences("daily alarm", MODE_PRIVATE).getString("tv_time", "시간"));
        alarm.setChecked(getSharedPreferences("daily alarm", MODE_PRIVATE).getBoolean("checked", false));

        final Calendar calendar = Calendar.getInstance();

        alarm = findViewById(R.id.alarm);
        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                        if (hourOfDay > 12) {
                            tv_alarm.setText("오후 " + (hourOfDay - 12) + "시" + minute + "분");
                            editor.putString("tv_time", "오후 " + (hourOfDay - 12) + "시" + minute + "분");
                        } else if (hourOfDay == 12) {
                            tv_alarm.setText("오후 " + (hourOfDay) + "시" + minute + "분");
                            editor.putString("tv_time", "오후 " + (hourOfDay) + "시" + minute + "분");
                        } else if (hourOfDay == 0) {
                            tv_alarm.setText("오전 " + (hourOfDay + 12) + "시" + minute + "분");
                            editor.putString("tv_time", "오전 " + (hourOfDay + 12) + "시" + minute + "분");
                        } else {
                            tv_alarm.setText("오전 " + hourOfDay + "시" + minute + "분");
                            editor.putString("tv_time", "오전 " + hourOfDay + "시" + minute + "분");
                        }
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1);
                        }
                        Log.d("시간 : ", String.valueOf(calendar.getTimeInMillis()));
                        editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                        editor.putBoolean("checked", true);
                        editor.apply();
                        diaryNotification(calendar, true);
                    }
                },hour, minute,android.text.format.DateFormat.is24HourFormat(mContext));
                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        alarm.setChecked(false);
                        tv_alarm.setText("시간");
                        SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                        editor.putString("tv_time", "시간");
                        editor.putBoolean("checked", false);
                        editor.apply();
                    }
                });
                if (isChecked) timePickerDialog.show();
                if (!isChecked) {
                    diaryNotification(calendar, false);
                    tv_alarm.setText("시간");
                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                    editor.putString("tv_time", "시간");
                    editor.putBoolean("checked", false);
                    editor.apply();
                }
            }
        });

        universe_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UniverseAccount.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog = new CustomDialog(settingActivity.this, "현재 학생정보를 지우시겠습니까?", btnok);
                customDialog.setCancelable(true);
                customDialog.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void diaryNotification(Calendar calendar, Boolean dailNotify) {
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 사용자가 매일 알람을 허용했다면
        if (dailNotify) {
            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else { //Disable Daily Notifications

            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
            }
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    private View.OnClickListener btnok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor editor = getSharedPreferences("school", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            customDialog.dismiss();
            Toast.makeText(mContext, "학생정보를 지웠습니다", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), VerfyType.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
    }
}
