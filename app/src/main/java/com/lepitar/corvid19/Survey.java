package com.lepitar.corvid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Survey extends AppCompatActivity {

    RadioButton rspns01,rspns01_2, rspns07,rspns07_1,
            rspns08, rspns08_1, rspns09, rspns09_1;

    CheckBox rspns02, rspns03, rspns05, rspns13, rspns14, rspns15, rspns04, rspns11;

    TextView prb1,prb2,prb3,prb4,prb5;
    ImageView setting;

    Button confirm;

    CustomDialog customDialog;

    String result, schulNm, stdntName;
    String text_1 = "1. 학생의 몸에 열이 있나요 ? (해당사항 선택)\n코로나19와 관계없이 평소에 발열 증상이 계속되는 경우는 제외";
    String text_2 = "2. 학생에게 코로나19가 의심되는 증상이 있나요 ? (모두 선택)\n단, 코로나19와 관계없이 평소에 증상이 계속되는 경우는 제외";
    String text_3 = "3. 학생이 최근(14일 이내) 해외여행을 다녀온 사실이 있나요 ?";
    String text_4 = "4. 동거가족 중 14일 이내 해외여행을 다녀온 사실이 있나요 ? \n(직업특성상 매번 해외 입출국하고 의심증상이 없는 경우는 제외)";
    String text_5 = "5. 동거가족 중 현재 자가격리 중 인 가족이 있나요 ?";

    String url = "";

    String check01,check02,check03,check04,check05,check07,check08,check09,check11,check13,check14,check15;

    /*
    2,3,4,5,6,10,11

    발열 증상 : rspns01 value : 37.5 미만 _2, 37.5 ~ 38 미만또는 발열감 2, 38도 이상 _3

    코로나 19 의심증상 : rspns02 아니오, 그룹(rspns03 기침,rspns04 인후통,rspns05 호흡곤란,rspns06 설사,rspns10 메스꺼움,rspns11 미각후각마비)

    학생 해외여행 : rspns07 value: 아니오, 예 _1

    동거가족 해외여행 : rspns08 value 아니오 0 예 _1

    동거가족 자가격리 : rspns09 value 아니오 0 예 _1

    (2번 코로나 19의심은 모든값이 1로 리턴됨)

    모두아니오
    1번 rspns01 1
    2번 rspns02 0
    3번 rspns07 0
    4번 rspns08 0
    5번 rspns09 0
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        //findView
        findView();
        //색상변경
        setColorText();
        //체크박스 리스터
        setCheckBoxListener();

        url = getSharedPreferences("school", MODE_PRIVATE).getString("website","");
        if (getSharedPreferences("school", MODE_PRIVATE).getBoolean("auto_submit",false)) {
            checkBoxValue();
            new Submit().execute();
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rspns02.isChecked() || rspns03.isChecked() || rspns04.isChecked()
                        || rspns05.isChecked() || rspns13.isChecked() || rspns14.isChecked() || rspns15.isChecked() || rspns11.isChecked()) {
                    if (rspns01_2.isChecked() || rspns03.isChecked() || rspns04.isChecked()
                    || rspns05.isChecked() || rspns13.isChecked() || rspns14.isChecked() || rspns15.isChecked() || rspns11.isChecked() || rspns07_1.isChecked() || rspns08_1.isChecked() || rspns09_1.isChecked()) {
                        customDialog = new CustomDialog(Survey.this, "코로나19 유증상 항목을 선택/응답하였습니다.\n응답 내용을 제출하시겠습니까?", btnok);
                        customDialog.setCancelable(true);
                        customDialog.show();
                    } else {
                        checkBoxValue();
                        new Submit().execute();
                    }
                } else {
                    Toast.makeText(Survey.this, "2번째 문항 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), settingActivity.class));
            }
        });
    }

    private class Submit extends AsyncTask<Void,Void,Void> {
        SharedPreferences sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String qstnCrtfcNoEncpt = sharedPreferences.getString("k","");
        ProgressDialog dialog = new ProgressDialog(Survey.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                submit(qstnCrtfcNoEncpt);
            } catch (IOException e) {
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
            if ("SUCCESS".equals(result)) {
                Intent intent = new Intent(getApplicationContext(), FinishActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("k", qstnCrtfcNoEncpt); //학생고유 인증값
                intent.putExtra("schulNm", schulNm); //학교이름
                intent.putExtra("stdntName", stdntName); //학생이름
                intent.putExtra("check01", check01); //문제1
                intent.putExtra("check02", check02); //문제2
                intent.putExtra("check03", check03); //문제2
                intent.putExtra("check04", check04); //문제2
                intent.putExtra("check05", check05);//문제2
                intent.putExtra("check07", check07);//문제2
                intent.putExtra("check08", check08);//문제3
                intent.putExtra("check09", check09);//문제4
                intent.putExtra("check11", check11);//문제5
                intent.putExtra("check13", check13);//문제2
                intent.putExtra("check14", check14);//문제2
                intent.putExtra("check15", check15);//문제2
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(Survey.this, "잘못된 본인확인 정보입니다.\n참여주소 또는 본인확인 정보를 확인바랍니다.\n확인 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void submit(String qstnCrtfcNoEncpt) throws Exception {
        Document doc = Jsoup.connect(url+"/stv_cvd_co01_000.do")
                .data("rtnRsltCode","SUCCESS","qstnCrtfcNoEncpt",qstnCrtfcNoEncpt
                ,"schulNm","","stdntName",""
                ,"rspns01",check01,"rspns02",check02,"rspns03",check03,"rspns04",check04,"rspns05",check05
                ,"rspns07",check07,"rspns08",check08,"rspns09",check09,"rspns11",check11,"rspns13",check13,"rspns14",check14,"rspns15",check15)
                .timeout(10000).ignoreContentType(true).get();
        JSONObject jsonObject = (JSONObject) new JSONObject(doc.text()).get("resultSVO");
        result = jsonObject.getString("rtnRsltCode");
        schulNm = jsonObject.getString("schulNm");
        stdntName = jsonObject.getString("stdntName");
    }

    public void findView() {
        setting = findViewById(R.id.setting);
        //라디오 버튼
        rspns01 = findViewById(R.id.rspns01);
        rspns01_2 = findViewById(R.id.rspns01_2);
        rspns07 = findViewById(R.id.rspns07);
        rspns07_1 = findViewById(R.id.rspns07_1);
        rspns08 = findViewById(R.id.rspns08);
        rspns08_1 = findViewById(R.id.rspns08_1);
        rspns09 = findViewById(R.id.rspns09);
        rspns09_1 = findViewById(R.id.rspns09_1);

        //체크박스
        rspns02 = findViewById(R.id.rspns02);
        rspns03 = findViewById(R.id.rspns03);
        rspns04 = findViewById(R.id.rspns04);
        rspns05 = findViewById(R.id.rspns05);
        rspns13 = findViewById(R.id.rspns13);
        rspns14 = findViewById(R.id.rspns14);
        rspns15 = findViewById(R.id.rspns15);
        rspns11 = findViewById(R.id.rspns11);

        //텍스트뷰
        prb1 = findViewById(R.id.prb1);
        prb2 = findViewById(R.id.prb2);
        prb3 = findViewById(R.id.prb3);
        prb4 = findViewById(R.id.prb4);
        prb5 = findViewById(R.id.prb5);

        confirm = findViewById(R.id.confirm);
    }

    public void setCheckBoxListener()
    {
        View.OnClickListener Listener = new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                switch (view.getId()) {
                    case R.id.rspns03:
                    case R.id.rspns05:
                    case R.id.rspns13:
                    case R.id.rspns14:
                    case R.id.rspns04:
                    case R.id.rspns11:
                    case R.id.rspns15:
                        rspns02.setChecked(false);
                        break;
                    case R.id.rspns02:
                        rspns03.setChecked(false);
                        rspns04.setChecked(false);
                        rspns05.setChecked(false);
                        rspns13.setChecked(false);
                        rspns14.setChecked(false);
                        rspns15.setChecked(false);
                        rspns11.setChecked(false);
                        break;
                }
            }
        };

        rspns02.setOnClickListener(Listener);
        rspns03.setOnClickListener(Listener);
        rspns04.setOnClickListener(Listener);
        rspns05.setOnClickListener(Listener);
        rspns13.setOnClickListener(Listener);
        rspns14.setOnClickListener(Listener);
        rspns15.setOnClickListener(Listener);
        rspns11.setOnClickListener(Listener);
    }

    public void setColorText() {
        SpannableStringBuilder tx_1 = new SpannableStringBuilder(text_1);
        SpannableStringBuilder tx_2 = new SpannableStringBuilder(text_2);
        SpannableStringBuilder tx_3 = new SpannableStringBuilder(text_3);
        SpannableStringBuilder tx_4 = new SpannableStringBuilder(text_4);
        SpannableStringBuilder tx_5 = new SpannableStringBuilder(text_5);
        tx_1.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 19, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_1.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 30, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_3.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 18, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_3.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_4.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 17, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_4.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx_5.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        prb1.setText(tx_1);
        prb2.setText(tx_2);
        prb3.setText(tx_3);
        prb4.setText(tx_4);
        prb5.setText(tx_5);
    }

    public void checkBoxValue() {
        //문제2같은경우는 null값 반환되기때문에 초기화를 시켜줌
        check02 = "";
        check03 = "";
        check04 = "";
        check05 = "";
        check13 = "";
        check14 = "";
        check15 = "";
        check11 = "";
        if (rspns01.isChecked()) { //문제1
            check01 = "1";
        }
        if (rspns01_2.isChecked()) { //문제1 2번
            check01 = "2";
        }
        if (rspns02.isChecked()) { //문제2
            check02 = "1";
        }
        if (rspns03.isChecked()) { //문제2 3
            check02 = "";
            check03 = "1";
        }
        if (rspns04.isChecked()) { //문제2 4
            check02 = "";
            check04 = "1";
        }
        if (rspns05.isChecked()) { //문제2 5
            check02 = "";
            check05 = "1";
        }
        if (rspns13.isChecked()) { //문제2 6
            check02 = "";
            check13 = "1";
        }
        if (rspns14.isChecked()) { //문제2 7
            check02 = "";
            check14 = "1";
        }
        if (rspns15.isChecked()) { //문제2 8
            check02 = "";
            check15 = "1";
        }
        if (rspns11.isChecked()) { //문제2 8
            check02 = "";
            check11 = "1";
        }
        if (rspns07.isChecked()) { //문제3
            check07 = "0";
        }
        if (rspns07_1.isChecked()) { //문제3 아니오
            check07 = "1";
        }
        if (rspns08.isChecked()) { //문제4 예
            check08 = "0";
        }
        if (rspns08_1.isChecked()) { //문제4 아니오
            check08 = "1";
        }
        if (rspns09.isChecked()) { //문제5 예
            check09 = "0";
        }
        if (rspns09_1.isChecked()) { //문제5 아니오
            check09 = "1";
        }
    }

    private View.OnClickListener btnok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkBoxValue();
            new Submit().execute();
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

    @Override
    public void onBackPressed() {
        FinsihDialog customDialog = new FinsihDialog(this);
        customDialog.setCancelable(true);
        customDialog.show();
    }
}
