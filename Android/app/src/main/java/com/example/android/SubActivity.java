package com.example.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.internal.TextWatcherAdapter;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubActivity extends AppCompatActivity {
    Button record_button, memo_button, summary_button;

    String uriName;

    InputMethodManager inputMethodManager;

    String TAG = "aa";

    LinearLayout subView;

    EditText record_name;

    TextView record_date, record_time;

    String dateFormat;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    RecordFragment recordFragment;
    MemoFragment memoFragment;
    SummaryFragment summaryFragment;

    MediaPlayer mediaPlayer = null;

    File newNamedFile = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent subIntent = getIntent();
        uriName = subIntent.getStringExtra("uriName");

        subView = (LinearLayout) findViewById(R.id.subView);


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

        record_name = (EditText) findViewById(R.id.record_name);

        //이름 저장
        String fileName = uriName.split("/")[uriName.split("/").length - 1 ];

        record_name.setText(fileName);

        //record_name 변경후 화면 터치시 키보드 내려가고 파일 이름 변경 됨
        subView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Editable newRecordName = record_name.getText();
                Path oldPath = Paths.get(uriName);
                uriName = uriName.replaceAll(fileName, String.valueOf(newRecordName));
                Path newPath = Paths.get(uriName);
                Log.d(TAG, String.valueOf(uriName));
                try {
                    Files.move(oldPath,newPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(record_name.getWindowToken(), 0);
                record_name.clearFocus();

                return false;
            }
        });


        record_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(record_name.getWindowToken(), 0);
                    record_name.clearFocus();
                    handled = true;
                }
                return handled;
            }
        });


        Log.d(TAG,"uriname"+uriName);

        mediaPlayer = new MediaPlayer();

        try {
            Log.d(TAG, "file"+file.getName());
            if (newNamedFile != null){
                mediaPlayer.setDataSource(newNamedFile.getAbsolutePath());
            }
            else {
                mediaPlayer.setDataSource(file.getAbsolutePath());
            }
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        record_time = (TextView) findViewById(R.id.record_time);

        record_time.setText(String.valueOf((double)mediaPlayer.getDuration()/1000) + "s");

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


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Log.d(TAG,"here");
//        MainActivity.onCreate()
//    }
}

