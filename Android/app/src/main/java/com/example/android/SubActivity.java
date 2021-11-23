package com.example.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubActivity extends AppCompatActivity {
    Button record_button, memo_button, summary_button;

    ImageButton delete_record;

    public static String uriName;
    String fileName;

    InputMethodManager inputMethodManager;

    String TAG = "aa";

    LinearLayout subView;

    EditText record_name;

    TextView record_date, record_time;

    String dateFormat;
    public static Boolean isPlaying = false;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    RecordFragment recordFragment;
    MemoFragment memoFragment;
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
        mediaPlayer = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        //MainActivity에서 녹음 uri 받아옴
        Intent subIntent = getIntent();
        uriName = subIntent.getStringExtra("uriName");

        subView = (LinearLayout) findViewById(R.id.subView);

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
            Log.d(TAG, "file"+file.getName());
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

        record_time.setText(String.valueOf((double)mediaPlayer.getDuration()/1000) + "s");


        //프래그먼트 구성
        record_button = (Button) findViewById(R.id.record_button);
        memo_button = (Button) findViewById(R.id.memo_button);
        summary_button = (Button) findViewById(R.id.summary_button);

        recordFragment = new RecordFragment();
        memoFragment = new MemoFragment();
        summaryFragment = new SummaryFragment();

        setFrag("RecordFragment");
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


//    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("http://34.64.68.234:8080/api/chgSphToTxt/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//    RetrofitService service = retrofit.create(RetrofitService.class);
//    String path = "/storage/emulated/0/Android/data/com.example.android/files/토플 지문.wav";
//
//    File file = new File(path);
//    RequestBody fileBody = RequestBody.create(MediaType.parse("audio/*"), file);
//
//    //서버에서 받는 키값,파일 이름 string, request body 객체
//    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", URLEncoder.encode(file.getName(), "utf-8"), fileBody);
//    Call<STTPostResult> call = service.request(filePart);
//
//    List<STTPostResult> results = (List<STTPostResult>) call.execute().body();



    //call.


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

    private void stopAudio(){
        SubActivity.mediaPlayer.stop();
        SubActivity.mediaPlayer.reset();
        SubActivity.mediaPlayer.release();

        SubActivity.isPlaying = false;
    }


}

