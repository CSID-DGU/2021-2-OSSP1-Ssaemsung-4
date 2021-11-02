package com.example.android;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton recordButton;
    //ImageButton recordStopButton;
    RecordDialog recordDialog;

    String TAG = "aa";

    //오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static int PERMISSION_CODE = 21;

    //오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName;
    private boolean isRecording = false;
    private Uri audioUri = null;

    //오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    ImageButton playIcon;
    SeekBar preSeekBar = null;
    SeekBar curSeekBar = null;

    //리사이클러뷰
    private AudioAdapter audioAdapter;
    private ArrayList<Uri> audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        recordButton = (ImageButton) findViewById(R.id.record_button);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //레코드 화면 생성
                RecordDialog recordDialog = new RecordDialog(MainActivity.this);
                recordDialog.callFunction();

                //버튼 누를시 녹음 시작
                if(checkAudioPermission()) {
                    if(isPlaying){
                        stopAudio();
                    }

                    Log.d(TAG,"start record");
                    isRecording = true;
                    startRecording();
                }

                //녹음 화면에서 버튼 누를 시 녹음 종료
                recordDialog.record_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"stop record");
                        isRecording = false;
                        stopRecording();
                        recordDialog.destroyDialog();
                    }
                });
            }
        });

        //리사이클러뷰
        RecyclerView audioRecyclerView = findViewById(R.id.audioRecyclerView);
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        audioRecyclerView.setAdapter(audioAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);



        //하나의 item 클릭시 -> 하나의 녹음파일 페이지로 변환 필요 수정 필요
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position, SeekBar seekbar) {

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
                        stopAudio();

                        //새로 파일 재생하기
                        playIcon = (ImageButton)view;
                        playAudio(file);
                    }
                } else {
                    //전에 플레이어 정지시켜놓고 다른 플레이어 시작시
                    if(preSeekBar != null && preSeekBar != curSeekBar){
                        Log.d(TAG,"preseekbar");
                        preSeekBar.setProgress(0);
                        stopAudio();
                    }
                    playIcon = (ImageButton) view;
                    playAudio(file);
                }
            }
        });

    }
    //init 끝

    //seekbar 변경 thread
    public void Thread(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while(isPlaying) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mediaPlayer == null) {
                        break;
                    }
                    curSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void playAudio(File file) {
        playIcon.setImageResource(R.drawable.ic_pause);
        //일시정지 후 시작하는 경우
        if ( mediaPlayer != null) {
            Log.d(TAG, "nullxxxx");
            int media_position = mediaPlayer.getCurrentPosition();
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
                stopAudio();
            }
        });

        Thread();
    }

    private void stopAudio() {
        //전에 실행시키던 seekbar 초기화
        if (preSeekBar != null) {
            Log.d(TAG, "preseekbar");
            preSeekBar.setProgress(0);
        }
        playIcon.setImageResource(R.drawable.ic_play);
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
        audioFileName = recordPath + "/" + "RecordExample_" + timeStamp;

        //Media Recorder 생성 및 설정
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //녹음 시작
        mediaRecorder.start();
    }

    private void stopRecording() {
        //녹음 종료
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;

        //파일이름을 uri로 변환해서 저장
        audioUri = Uri.parse(audioFileName);

        Log.d(TAG, audioUri.toString());
        audioList.add(audioUri);

        Log.d(TAG, audioList.toString());
        //데이터 갱신
        audioAdapter.notifyDataSetChanged();
    }


}