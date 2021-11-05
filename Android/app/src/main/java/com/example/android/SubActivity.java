package com.example.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubActivity extends AppCompatActivity {
    Button record_button, memo_button, summary_button;

    EditText record_name;

    TextView record_date, record_time;

    String dateFormat;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    RecordFragment recordFragment;
    MemoFragment memoFragment;
    SummaryFragment summaryFragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent subIntent = getIntent();
        String uriName = subIntent.getStringExtra("uriName");

        record_name = (EditText) findViewById(R.id.record_name);

        //이름 저장
        record_name.setText(uriName.split("/")[uriName.split("/").length - 1 ]);

        record_date = (TextView) findViewById(R.id.record_date);

        dateFormat = (String) record_date.getText();

        File file = new File(uriName);
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime time = attributes.creationTime();

            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            dateFormat = simpleDateFormat.format(new Date(time.toMillis()));
        }catch (IOException e) {
            e.printStackTrace();
        }
        record_date.setText(dateFormat);

        record_time = (TextView) findViewById(R.id.record_time);

        record_button = (Button) findViewById(R.id.record_button);
        memo_button = (Button) findViewById(R.id.memo_button);
        summary_button = (Button) findViewById(R.id.summary_button);

        recordFragment = new RecordFragment();
        memoFragment = new MemoFragment();
        summaryFragment = new SummaryFragment();

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag("RecordFragment");
            }
        });

        memo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag("MemoFragment");
            }
        });

        summary_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag("SummaryFragment");
            }
        });
    }

    //아래 세가지 옵션에 따른 화면 구성
    public void setFrag(String fragName) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragName) {
            case "RecordFragment" :
                fragmentTransaction.replace(R.id.frameView, recordFragment);
                fragmentTransaction.commit();
                break;
            case "MemoFragment" :
                fragmentTransaction.replace(R.id.frameView, memoFragment);
                fragmentTransaction.commit();
                break;
            case "SummaryFragment" :
                fragmentTransaction.replace(R.id.frameView, summaryFragment);
                fragmentTransaction.commit();
                break;
        }
    }
}