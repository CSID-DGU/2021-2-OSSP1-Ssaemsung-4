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
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;

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
    ImageButton playIcon;
    SeekBar preSeekBar = null;
    SeekBar curSeekBar = null;

    public SoundVisualizerView soundVisualizerView;

    //리사이클러뷰
    private AudioAdapter audioAdapter;
    private ArrayList<Uri> audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //레코드 화면 생성
                RecordDialog recordDialog = new RecordDialog(MainActivity.this);
                //버튼 누를시 녹음 시작
                if(checkAudioPermission()) {
                    if(isPlaying){
                        stopAudio();
                    }


                    Log.d(TAG,"start record");
                    isRecording = true;
                    startRecording();
                }
                recordDialog.callFunction();

                soundVisualizerView = recordDialog.soundVisualizerView;

//                if(mediaRecorder != null){
//                    soundVisualizerView.onRequestCurrentAmplitude = mediaRecorder.getMaxAmplitude();
//                }else {
//                    soundVisualizerView.onRequestCurrentAmplitude = 0;
//                }

                soundVisualizerView.startVisualizing(false);
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





                //파일 불러오기
                recordDialog.update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ///storage/emulated/0/Download/2001_3subway2.mp3
                        String sdPath = "/storage/emulated/0/Download/";
                        //String sdPath = "/storage/emulated/0/Android/data/com.example.android/files/";
                        //String sdPath = getAp
                        Log.d(TAG,"sdPath"+ sdPath);

                        File pa = new File(sdPath);

                        if(pa.exists()){
                            Log.d(TAG,"aaaaaaaaaaaaaaaaaaaaaa");
                            String[] list = pa.list();
                            Log.d(TAG, String.valueOf(list.length));

                        }

                        File sdDir = new File(sdPath);
                        String[] sdFileListNames = sdDir.list();

                        String sdFileName = sdPath + "sdFileListNames[which]";
                        String recordPath = getExternalFilesDir("/").getAbsolutePath();
                        audioFileName = recordPath +"2001_3subway2.mp3";
                        //audioFileName = recordPath + "/" + sdFileListNames[which];
                        File sdFile = new File("/storage/emulated/0/Download/2001_3subway2.mp3");
                        File newFile = new File(audioFileName);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try {
                                Files.copy(sdFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                //Files.copy("/storage/emulated/0/Download/2001_3subway2.mp3", newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("불러올 파일을 선택하세요");
                        //builder.setMessage();

                        builder.setItems(sdFileListNames, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sdFileName = sdPath + sdFileListNames[which];
                                String recordPath = getExternalFilesDir("/").getAbsolutePath();
                                audioFileName = recordPath + "/" + sdFileListNames[which];

                                File sdFile = new File(sdFileName);
                                File newFile = new File(audioFileName);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    try {
                                        Files.copy(sdFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                audioUri = Uri.parse(audioFileName);

                                audioList.add(audioUri);
                                //데이터 갱신
                                audioAdapter.notifyDataSetChanged();

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });

                ///sdcard/Download/2001_3subway2.mp3
                recordDialog.bookmark_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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



        //하나의 item 클릭시 -> 하나의 녹음파일 페이지로 변환 필요 수정 필요
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position, SeekBar seekbar) {

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

        //각 녹음별 화면으로 이동
        audioAdapter.setOnNameClickListener(new AudioAdapter.OnNameClickListner() {
            @Override
            public void onNameClick(View view, int position) {
                String uriName = String.valueOf(audioList.get(position));

                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("uriName", uriName);
                startActivity(intent);
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


    //seekbar 변경 thread
    public void Thread(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
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
        curSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


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
        audioUri = Uri.parse(audioFileName);

        audioList.add(audioUri);

        //데이터 갱신
        audioAdapter.notifyDataSetChanged();
    }
}