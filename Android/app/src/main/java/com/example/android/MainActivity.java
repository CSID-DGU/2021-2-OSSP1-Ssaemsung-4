package com.example.android;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    public static boolean isCancel;

    ImageButton recordButton;
    //ImageButton recordStopButton;
    RecordDialog recordDialog;

    String TAG = "aa";

    //오디오 권한onItemClick
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static int PERMISSION_CODE = 21;

    //오디오 파일 녹음 관련 변수
    public static MediaRecorder mediaRecorder;
    public static String audioFileName;
    private boolean isRecording = false;
    private Uri audioUri = null;

    //오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    ImageButton playIcon = null;
    SeekBar preSeekBar = null;
    SeekBar curSeekBar = null;

    public SoundVisualizerView soundVisualizerView;

    //리사이클러뷰
    private AudioAdapter audioAdapter;
    protected static ArrayList<Uri> audioList;

    BookMarkDBHelper bookMarkDBHelper;
    STTDBHelper sttDBHelper;
    SQLiteDatabase bookmarkDB;
    SQLiteDatabase sttDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bookmarkdatabase 생성
        bookMarkDBHelper = new BookMarkDBHelper(MainActivity.this, "bookmarkDatabase.db", null, 1);
        bookmarkDB = bookMarkDBHelper.getWritableDatabase();
        bookMarkDBHelper.onCreate(bookmarkDB);

        sttDBHelper = new STTDBHelper(MainActivity.this, "sttDatabase.db", null,1);
        sttDB = sttDBHelper.getWritableDatabase();
        sttDBHelper.onCreate(sttDB);

        mContext = this;

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        recordButton = (ImageButton) findViewById(R.id.record_button);

        // 코드 확인 필요
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //레코드 화면 생성
                if(isCancel == true){
                    stopRecording();
                    Uri uriName = Uri.parse(MainActivity.audioFileName);
                    File file = new File(String.valueOf(uriName));
                    file.delete();
                }
                RecordDialog recordDialog = new RecordDialog(MainActivity.this);
                recordDialog.callFunction();
                //버튼 누를시 녹음 시작
                if(checkAudioPermission()) {
                    if(isPlaying){
                        stopAudio(null);
                    }
                    Log.d(TAG,"start record");
                    isRecording = true;

                    isCancel =  false;
                    startRecording();

                    recordDialog.startCountup();

                    soundVisualizerView = recordDialog.soundVisualizerView;
                    soundVisualizerView.startVisualizing(false);
                }

                //녹음 화면에서 버튼 누를 시 녹음 종료
                recordDialog.record_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"stop record");
                        isRecording = false;
                        stopRecording();
                        recordDialog.stopCountup();
                        recordDialog.clearCountTime();
                        recordDialog.destroyDialog();
                    }
                });



                //파일 불러오기
                recordDialog.update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        recordDialog.cancelDialog();

                        //avd -> setting 가서 permission manager 들어가서 storage 어플 허용해주면 됨
                        //String sdPath = "/storage/emulated/0/Download/";
                        File sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                        //String sdPath =  "/mnt/sdcard/Download/sdcard/emulater/0/Download";



                        Log.d("pathhere", String.valueOf(sdPath));


                        //File sdDir = new File(sdPath);
                        //String[] sdFileListNames = sdDir.list();
                        String[] sdFileListNames = sdPath.list();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("불러올 파일을 선택하세요");
                        //builder.setMessage();

                        builder.setItems(sdFileListNames, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sdFileName = String.valueOf(sdPath) + "/" + sdFileListNames[which];
                                String recordPath = getExternalFilesDir("/").getAbsolutePath();
                                audioFileName = recordPath + "/" + sdFileListNames[which];
                                //audioFileName = recordPath + "/" + "dfsdfasfasf.wav";

                                File sdFile = new File(sdFileName);
                                File newFile = new File(audioFileName);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    try {
                                        Log.d("path", String.valueOf(sdFile.toPath()) + newFile.toPath());
                                        Files.copy(sdFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                audioUri = Uri.parse(audioFileName);

                                audioList.add(audioUri);
                                //데이터 갱신
                                audioAdapter.notifyDataSetChanged();

                                PostSTTResult(audioFileName);

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });

                recordDialog.bookmark_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int bookmarkTime = recordDialog.getBookMarkTime();
                        //Log.d("book", String.valueOf(bookmarkTime));
                        String sql = "INSERT INTO bookmarkTable('record_name','time') values('" + audioFileName + "','" + bookmarkTime + "');";
                        bookmarkDB.execSQL(sql);
                    }
                });

            }
        });

        //리사이클러뷰
        RecyclerView audioRecyclerView = findViewById(R.id.audioRecyclerView);
        File recordDirectory = new File(getExternalFilesDir("/").getAbsolutePath());
        File[] recordFiles = recordDirectory.listFiles();

        audioList = new ArrayList<>();

        for(int i =0; i<recordFiles.length; i++) {
            audioList.add(Uri.parse(String.valueOf(recordFiles[i])));
        }



        audioAdapter = new AudioAdapter(this, audioList);
        audioRecyclerView.setAdapter(audioAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);



        //하나의 item 클릭시 -> play 버튼 누를시
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position, SeekBar seekbar, TextView currenttimeText) {

                //uriname -> /storage/emulated/0/Android/data/com.example.android/files/RecordExample_20211105_060234
                String uriName = String.valueOf(audioList.get(position));

                File file = new File(uriName);

                preSeekBar = curSeekBar;
                curSeekBar = seekbar;

                if (isPlaying) {
                    if (playIcon == view) {
                        //같은 파일을 클릭했을 경우
                        pauseAudio();
                    } else {
                        //다른 음성 파일을 클릭했을 경우
                        //기존의 재생중인 파일 중지
                        stopAudio(currenttimeText);

                        //새로 파일 재생하기
                        playIcon = (ImageButton)view;
                        playAudio(file, currenttimeText);
                    }
                } else {
                    //전에 플레이어 정지시켜놓고 다른 플레이어 시작시
                    if(preSeekBar != null && preSeekBar != curSeekBar){
                        //Log.d(TAG,"preseekbar");
                        preSeekBar.setProgress(0);
                        stopAudio(currenttimeText);
                    }
                    playIcon = (ImageButton) view;
                    playAudio(file, currenttimeText);
                }
            }
        });

        audioAdapter.setOnSeekBarChangeListener(new AudioAdapter.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarChange(int position, SeekBar seekbar, int progress, TextView currenttimeText) {
                String uriName = String.valueOf(audioList.get(position));

                File file = new File(uriName);

                preSeekBar = curSeekBar;
                curSeekBar = seekbar;

                if (isPlaying) {
                    stopAudio(currenttimeText);
                }

                MediaPlayer tempMediaPlayer = new MediaPlayer();

                try {
                    tempMediaPlayer.setDataSource(file.getAbsolutePath());
                    tempMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(tempMediaPlayer != mediaPlayer) {
                    mediaPlayer = tempMediaPlayer;
                }

                curSeekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.seekTo(progress);
                setTimeText(currenttimeText, mediaPlayer.getCurrentPosition());
                //Log.d("seek", mediaPlayer.getDuration() + String.valueOf(progress));


            }
        });

        //각 녹음별 화면으로 이동
        audioAdapter.setOnNameClickListener(new AudioAdapter.OnNameClickListener() {
            @Override
            public void onNameClick(View view, int position) {
                String uriName = String.valueOf(audioList.get(position));

                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("uriName", uriName);
                startActivity(intent);
                if(mediaPlayer != null){
                    stopAudio(null);
                }

            }
        });

        audioAdapter.setOnReplayClickListener(new AudioAdapter.OnReplayClickListener() {
            @Override
            public void onReplayClick(View view, int position) {
//                stopAudio();
//                playAudio();

            }
        });

        audioAdapter.setOnExpandClickListener(new AudioAdapter.OnExpandClickListener() {
            @Override
            public void onExpandClick(int position, TextView currenttimeText, TextView endtimeText) {
                String uriName = String.valueOf(audioList.get(position));

                File file = new File(uriName);

                MediaPlayer newMediaPlayer = new MediaPlayer();

                try {
                    newMediaPlayer.setDataSource(file.getAbsolutePath());
                    newMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(mediaPlayer == null) {
                    setTimeText(currenttimeText, 0);
                }else {
                    setTimeText(currenttimeText, mediaPlayer.getDuration());
                }
                Log.d("end", String.valueOf(newMediaPlayer.getDuration()));
                //setTimeText(endtimeText,newMediaPlayer.getDuration());


            }
        });
        audioAdapter.setOnTrashClickListener(new AudioAdapter.OnTrashClickListener() {
            @Override
            public void onTrashClick(View view, int position) {
                String uriName = String.valueOf(audioList.get(position));
                String fileName = uriName.split("/")[uriName.split("/").length - 1 ];

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제");
                builder.setMessage(fileName + "을 삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(uriName);
                        file.delete();

                        String audioFileName = uriName;
                        String sql = "DELETE FROM bookmarkTable WHERE record_name='" + audioFileName + "';";
                        bookmarkDB.execSQL(sql);

                        sql = "DELETE FROM sttTable WHERE record_name='" + audioFileName + "';";
                        sttDB.execSQL(sql);

                        onResume();


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


    }
    //init 끝



    public static int getMaxAmplitude(){
        if (mediaRecorder != null){
            return mediaRecorder.getMaxAmplitude();
        }
        return 0;
    }

    public void setTimeText(TextView timeText, int time){
        time /= 1000;
        int minutes = time / 60;
        int seconds = time % 60;
        timeText.setText(String.format("%02d:%02d", minutes,seconds));
        return ;
    }




    //seekbar 변경 thread
    public void Thread(TextView currenttimeText){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if(!isPlaying){
                    return ;
                }
                while(isPlaying) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mediaPlayer == null) {
                        break;
                    }
                    curSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    runOnUiThread(new Runnable(){
                        public void run() {
                            //Log.d("here", String.valueOf(mediaPlayer.getCurrentPosition()));
                            setTimeText(currenttimeText, mediaPlayer.getCurrentPosition());
                        }
                    });

                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void playAudio(File file, TextView currenttimeText) {
        playIcon.setImageResource(R.drawable.ic_pause);
        //일시정지 후 시작하는 경우
        if ( mediaPlayer != null) {
            Log.d(TAG, "nullxxxx");
            //int media_position = mediaPlayer.getCurrentPosition();
            int media_position = curSeekBar.getProgress();

            mediaPlayer.seekTo(media_position);
            mediaPlayer.start();
        }
        // 아예 처음 시작하는 부분
        else {
            Log.d(TAG, "null");
            mediaPlayer= new MediaPlayer();
            Log.d(TAG,"play audio");

            try {
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        isPlaying = true;
        curSeekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio(currenttimeText);
            }
        });

        Thread(currenttimeText);
    }

    private void stopAudio(TextView currenttimeText) {
        //전에 실행시키던 seekbar 초기화

        if (preSeekBar != null) {
            Log.d(TAG, "preseekbar");
            preSeekBar.setProgress(0);
        }
        if ( playIcon != null){
            playIcon.setImageResource(R.drawable.ic_play);
        }

        isPlaying = false;
        if(mediaPlayer != null){
            Log.d(TAG,"play stop audio");
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        curSeekBar.setProgress(0);

        mediaPlayer = null;
        Log.d(TAG,"stop audio");
        if(currenttimeText != null){
            setTimeText(currenttimeText,0);
        }


    }

    private void pauseAudio() {
        playIcon.setImageResource(R.drawable.ic_play);
        isPlaying = false;
        Log.d(TAG,"pause audio");
        mediaPlayer.pause();
        curSeekBar.setProgress(mediaPlayer.getCurrentPosition());
    }

    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    private void startRecording() {
        //파일의 외부 경로 확인
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFileName = recordPath + "/" + "Record_" + timeStamp + ".wav";

        mediaRecorder = null;
        //Media Recorder 생성 및 설정
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioChannels(2);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(192000);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //녹음 시작

        //soundVisualizerView.startVisualizing(false);
    }

    private void stopRecording() {
        //녹음 종료
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;

        soundVisualizerView.stopVisualizing();
        soundVisualizerView.clearVisualization();
        //soundVisualizerView = null;r

        //파일이름을 uri로 변환해서 저장
        if(isCancel == false){
            audioUri = Uri.parse(audioFileName);

            audioList.add(audioUri);

            //데이터 갱신
            audioAdapter.notifyDataSetChanged();

            PostSTTResult(audioFileName);
        }

    }

    //stt 서버와 연결후 db 에 msg로그 저장
    private void PostSTTResult(String audioFileName){
        Log.d("stt", "sttstart");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30,TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://34.64.68.234:8080/api/chgSphToTxt/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        String path = audioFileName;

        File file = new File(path);
        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/*"), file);

        //서버에서 받는 키값,파일 이름 string, request body 객체
        MultipartBody.Part filePart = null;
        try {
            filePart = MultipartBody.Part.createFormData("file", URLEncoder.encode(file.getName(), "utf-8"), fileBody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<STTPostResult> call = service.request(filePart);
        call.enqueue(new Callback<STTPostResult>() {
            @Override
            public void onResponse(Call<STTPostResult> call, Response<STTPostResult> response) {
                if(response.isSuccessful()) {
                    STTPostResult result = response.body();
                    String[] msg_log = result.getMsg_log();
                    String msg = result.toString();
                    Log.d("msg", msg);
                    //null 값 반환
                    //D/msg: PostResult{start_timenullmsg_lognulltextnullspeaker_nonull}
                    if(msg_log == null){
                        return ;
                    }
                    for(int i=0; i< msg_log.length; i++){
                        Log.d("log", msg_log[i]);
                        String sql = "INSERT INTO sttTable('record_name','msg_log') values('" + audioFileName + "','" + msg_log[i].replace("'","") + "');";
                        sttDB.execSQL(sql);
                    }
                    Log.d("server", "success");
                }else {
                    Log.d("fail", "fail");
                }
            }

            @Override
            public void onFailure(Call<STTPostResult> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}