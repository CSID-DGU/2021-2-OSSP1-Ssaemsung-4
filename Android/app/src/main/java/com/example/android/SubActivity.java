package com.example.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SubActivity extends AppCompatActivity {
    Button record_button, bookmark_button, summary_button;

    ImageButton delete_record;

    public static String uriName;

    public static ArrayList<String> sttList;
    public static ArrayList<String> speakerList;
    public static ArrayList<String> copysttList;

    public static boolean isFinish = false;

    public static String fragmentMode;


    String fileName;

    InputMethodManager inputMethodManager;

    String TAG = "aa";

    LinearLayout subView;
    LinearLayout speakerView;

    EditText record_name;

    TextView record_date, record_time;

    String dateFormat;
    public static Boolean isPlaying = false;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    RecordFragment recordFragment;
    BookMarkFragment bookMarkFragment;
    SummaryFragment summaryFragment;

    public static MediaPlayer mediaPlayer = null;

    BookMarkDBHelper helper;
    SQLiteDatabase db;

    BookMarkDBHelper bookMarkDBHelper;
    STTDBHelper sttDBHelper;
    SQLiteDatabase bookmarkDB;
    SQLiteDatabase sttDB;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        isFinish = false;

        bookMarkDBHelper = new BookMarkDBHelper(SubActivity.this, "bookmarkDatabase.db", null, 1);
        bookmarkDB = bookMarkDBHelper.getWritableDatabase();
        bookMarkDBHelper.onCreate(bookmarkDB);

        sttDBHelper = new STTDBHelper(SubActivity.this, "sttDatabase.db", null,1);
        sttDB = sttDBHelper.getWritableDatabase();
        sttDBHelper.onCreate(sttDB);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isPlaying == true){
            stopAudio();
        }
        mediaPlayer = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isPlaying == true){
            stopAudio();
        }
        mediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isPlaying == true){
            stopAudio();
        }
        isFinish = true;
        mediaPlayer = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        //MainActivity에서 녹음 uri 받아옴
        Intent subIntent = getIntent();
        uriName = subIntent.getStringExtra("uriName");

        subView = (LinearLayout) findViewById(R.id.subView);
        speakerView = (LinearLayout) findViewById(R.id.speakerView);

        record_date = (TextView) findViewById(R.id.record_date);

        dateFormat = (String) record_date.getText();

        File file = new File(uriName);

        //file 날짜 지정
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

        mediaPlayer = new MediaPlayer();


        try {
            //Log.d(TAG, "file"+file.getName());
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        record_date.setText(dateFormat);

        record_name = (EditText) findViewById(R.id.record_name);

        //이름 저장
        fileName = uriName.split("/")[uriName.split("/").length - 1 ];

        record_name.setText(fileName);

        //(만약 키보드나왓을시 다른 화면 선택시) record_name 변경후 화면 터치시 키보드 내려가고 파일 이름 변경 됨
        subView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Editable newRecordName = record_name.getText();
                Path oldPath = Paths.get(uriName);
                String pasturiName = uriName;
                uriName = uriName.replaceAll(fileName, String.valueOf(newRecordName));
                Path newPath = Paths.get(uriName);
                Log.d(TAG, String.valueOf(uriName));
                try {
                    Files.move(oldPath,newPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                file.delete();
                String pastaudioFileName = pasturiName;
                String curaudioFileName = uriName;
                String sql = "UPDATE bookmarkTable SET record_name='" + curaudioFileName + "' WHERE record_name='" + pastaudioFileName + "';";
                bookmarkDB.execSQL(sql);

                sql = "UPDATE sttTable SET record_name='" + curaudioFileName + "' WHERE record_name='" + pastaudioFileName + "';";
                sttDB.execSQL(sql);

                File file = new File(uriName);
                fileName = uriName.split("/")[uriName.split("/").length - 1 ];
                Log.d(TAG, "filename  " + file.getName());
                inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(record_name.getWindowToken(), 0);
                record_name.clearFocus();

                return false;
            }
        });

        delete_record = (ImageButton)findViewById(R.id.delete_record);

        //삭제버튼 눌럿을시 alertdialog 생성 file 삭제 기능
        delete_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
                builder.setTitle("삭제");
                builder.setMessage(fileName + "을 삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(uriName);
                        file.delete();

                        String audioFileName = uriName;
                        String sql = "DELETE FROM bookmarkTable WHERE record_name='" + audioFileName + "';";
                        db.execSQL(sql);

                        sql = "DELETE FROM sttTable WHERE record_name='" + audioFileName + "';";
                        sttDB.execSQL(sql);

                        finish();

                    }
                });
                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        record_time = (TextView) findViewById(R.id.record_time);

        int time = mediaPlayer.getDuration();
        time /= 1000;
        int minutes = time / 60;
        int seconds = time % 60;

        record_time.setText(String.format("%02d:%02d", minutes,seconds));

        //프래그먼트 구성
        record_button = (Button) findViewById(R.id.record_button);
        bookmark_button = (Button) findViewById(R.id.bookmark_button);
        summary_button = (Button) findViewById(R.id.summary_button);

        Cursor cursor = sttDB.rawQuery("SELECT msg_log FROM sttTable WHERE record_name='" + SubActivity.uriName +"'", null);

        sttList = new ArrayList<>();

        while(cursor.moveToNext()){
            String msg_log = cursor.getString(0);
            //Log.d("msg", msg_log);
            sttList.add(msg_log);
        }

        copysttList = (ArrayList<String>) sttList.clone();

        //speakerbutton 만들기
        ArrayList<String> speakerLists = new ArrayList<>();
        for(int i =0; i<sttList.size(); i++){
            speakerLists.add(sttList.get(i).split(":")[0].split("-")[1]);
        }

        Set<String> set = new HashSet<String>(speakerLists);
        speakerList = new ArrayList<String>(set);

        recordFragment = new RecordFragment();
        bookMarkFragment = new BookMarkFragment();
        summaryFragment = new SummaryFragment();

        setFrag("RecordFragment");
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag("RecordFragment");
            }
        });

        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag("BookMarkFragment");
            }
        });

        summary_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag("SummaryFragment");
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);

        Button[] speakerButton = new Button[speakerList.size()];
        for(int i=0; i<speakerList.size(); i++){
            //Log.d("speaker", speakerList.get(i));
            speakerButton[i] = new Button(this);
            speakerButton[i].setAllCaps(false);
            speakerButton[i].setText(speakerList.get(i));
            speakerButton[i].setLayoutParams(params);
            int speakerNum = i;
            speakerButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sttList = null;
                    sttList = new ArrayList<>();
                    for(int j =0; j< copysttList.size(); j++){
                        //Log.d("st",copysttList.get(j));
                        //Log.d("sp", speakerList.get(speakerNum));
                        if(copysttList.get(j).contains(speakerList.get(speakerNum))){
                            //Log.d("in","in");
                            sttList.add(copysttList.get(j));
                        }

                    }
                    recordFragment.refreshList();


                }
            });
            speakerView.addView(speakerButton[i]);

        }


    }

    public void setFrag(String fragName) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragName) {
            case "RecordFragment" :
                fragmentMode = "RecordFragment";
                sttList = (ArrayList<String>) copysttList.clone();
                fragmentTransaction.replace(R.id.frameView, recordFragment);
                fragmentTransaction.commit();
                //recordFragment.refreshList();
                break;
            case "BookMarkFragment" :
                fragmentMode = "BookMarkFragment";
                fragmentTransaction.replace(R.id.frameView, bookMarkFragment);
                fragmentTransaction.commit();
                break;
            case "SummaryFragment" :
                fragmentMode = "SummaryFragment";
                fragmentTransaction.replace(R.id.frameView, summaryFragment);
                fragmentTransaction.commit();
                break;
        }

    }

    private void stopAudio(){
        SubActivity.mediaPlayer.stop();
        SubActivity.mediaPlayer.reset();
        SubActivity.mediaPlayer.release();

        SubActivity.isPlaying = false;
    }

    public void refreshFrag(String fragName){
        fragmentTransaction.detach(recordFragment).attach(recordFragment).commit();

    }


}

