package com.example.android;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

public class SummaryFragment extends Fragment {

    View view;

    RecyclerView summaryRecyclerView;
    public ArrayList<String> summaryList;

    public static SummaryAdapter summaryAdapter;

    ImageButton play_button;

    SeekBar seekBar;

    Button startTime_button;
    Button endTime_button;
    Button startSummary_button;

    TextView startTime_text;
    TextView endTime_text;

    SummaryDBHelper summaryDBHelper;
    static SQLiteDatabase summaryDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_summary, container, false);

        summaryDBHelper = new SummaryDBHelper(getActivity().getApplicationContext(), "summaryDatabase.db", null, 1);
        summaryDB = summaryDBHelper.getWritableDatabase();
        summaryDBHelper.onCreate(summaryDB);

        init();

        return view;
    }

    public void init(){

        play_button = (ImageButton) view.findViewById(R.id.play_button);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        startTime_button = (Button)view.findViewById(R.id.startTime_button);
        endTime_button = (Button)view.findViewById(R.id.endTime_button);
        startSummary_button = (Button)view.findViewById(R.id.startSummary_button);

        startTime_text = (TextView)view.findViewById(R.id.startTime_text);
        endTime_text = (TextView)view.findViewById(R.id.endTime_text);

        SubActivity.mediaPlayer = new MediaPlayer();
        File file = new File(SubActivity.uriName);
        try {
            SubActivity.mediaPlayer.setDataSource(file.getAbsolutePath());
            SubActivity.mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(SubActivity.mediaPlayer.getDuration());

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SubActivity.isPlaying == true){
                    pauseAudio();
//                    SubActivity.mediaPlayer = null;
//                    SubActivity.mediaPlayer = new MediaPlayer();
//                    File file = new File(SubActivity.uriName);
//                    try {
//                        SubActivity.mediaPlayer.setDataSource(file.getAbsolutePath());
//                        SubActivity.mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    playAudio();
                }
            }

        });
        Thread();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("a", "here");

                if(fromUser){
                    if(SubActivity.isPlaying){
                        pauseAudio();
                    }
                    SubActivity.mediaPlayer.seekTo(progress);
                    int time = SubActivity.mediaPlayer.getCurrentPosition();
                    time /= 1000;
                    int minutes = time / 60;
                    int seconds = time % 60;
                    if(startTime_button.isEnabled()){
                        startTime_text.setText(String.format("%02d:%02d", minutes,seconds));
                    }else {
                        endTime_text.setText(String.format("%02d:%02d", minutes,seconds));
                    }
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime_button.setEnabled(false);
                endTime_button.setEnabled(true);
            }
        });

        endTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTime_button.setEnabled(false);
                startSummary_button.setEnabled(true);
            }
        });

        startSummary_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int startTime = Integer.parseInt(startTime_text.getText().toString().split(":")[0]) * 60 + Integer.parseInt(startTime_text.getText().toString().split(":")[1]);
                int endTime = Integer.parseInt(endTime_text.getText().toString().split(":")[0]) * 60 + Integer.parseInt(endTime_text.getText().toString().split(":")[1]);
                if(startTime >= endTime){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("오류");
                    builder.setMessage("시간을 다시 설정해주세요");
                    startTime_button.setEnabled(true);
                    endTime_button.setEnabled(false);
                    startSummary_button.setEnabled(false);

                    startTime_text.setText("00:00");
                    endTime_text.setText("00:00");
                } else {
                    try {
                        PostSummaryResult(startTime, endTime);
                        summaryList.clear();

                        Cursor cursor = summaryDB.rawQuery("SELECT summary_name FROM summaryTable", null);

                        while(cursor.moveToNext()){
                            String summary_name = cursor.getString(0);
                            //Log.d("msg", msg_log);
                            summaryList.add(summary_name);
                        }

                        summaryAdapter.notifyDataSetChanged();
                        Log.d("server end ", "eneneenen");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        summaryRecyclerView = (RecyclerView) view.findViewById(R.id.summaryRecyclerView);

        summaryList = new ArrayList<String>();
        Cursor cursor = summaryDB.rawQuery("SELECT summary_name FROM summaryTable", null);

        while(cursor.moveToNext()){
            String summary_name = cursor.getString(0);
            //Log.d("msg", msg_log);
            summaryList.add(summary_name);
        }

        summaryAdapter = new SummaryAdapter(getActivity().getApplicationContext(), summaryList);
        summaryRecyclerView.setAdapter(summaryAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        summaryRecyclerView.setLayoutManager(mLayoutManager);




    }

    public static String setSummaryContext(String summary_name){
        Cursor cursor = summaryDB.rawQuery("SELECT summary_context FROM summaryTable WHERE summary_name='" + summary_name+"'", null);
        String summary_context = "";
        while(cursor.moveToNext()){
            summary_context = cursor.getString(0);
        }

        return summary_context;
    }

    private void playAudio(){
        play_button.setImageResource(R.drawable.ic_pause);
        if ( SubActivity.mediaPlayer != null) {
            int media_position = seekBar.getProgress();
            Log.d("aa", String.valueOf(media_position));

            SubActivity.mediaPlayer.seekTo(media_position);
            SubActivity.mediaPlayer.start();
        }else {
            Log.d("a", "null");
            SubActivity.mediaPlayer = new MediaPlayer();
            File file = new File(SubActivity.uriName);
            try {
                SubActivity.mediaPlayer.setDataSource(file.getAbsolutePath());
                SubActivity.mediaPlayer.prepare();
                SubActivity.mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        SubActivity.isPlaying = true;
        //seekBar.setMax(SubActivity.mediaPlayer.getCurrentPosition());
        SubActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        Thread();
    }
    private void pauseAudio() {
        play_button.setImageResource(R.drawable.ic_play);
        SubActivity.isPlaying = false;
        SubActivity.mediaPlayer.pause();
        Log.d("pause audio", String.valueOf(SubActivity.mediaPlayer.getCurrentPosition()));
        seekBar.setProgress(SubActivity.mediaPlayer.getCurrentPosition());

    }

    private void stopAudio(){
        Log.d("a", "stop");
        play_button.setImageResource(R.drawable.ic_play);
        seekBar.setProgress(0);
        SubActivity.isPlaying = false;

        SubActivity.mediaPlayer.stop();
        SubActivity.mediaPlayer.reset();
        SubActivity.mediaPlayer.release();

        SubActivity.mediaPlayer = null;

    }

    public void Thread(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!SubActivity.isPlaying) {
                    return;
                }
                while (SubActivity.isPlaying) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (SubActivity.mediaPlayer == null) {
                        break;
                    }
                    seekBar.setProgress(SubActivity.mediaPlayer.getCurrentPosition());
                    int time = SubActivity.mediaPlayer.getCurrentPosition();
                    time /= 1000;
                    int minutes = time / 60;
                    int seconds = time % 60;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(startTime_button.isEnabled()){
                                startTime_text.setText(String.format("%02d:%02d", minutes, seconds));
                            }else {
                                if(endTime_button.isEnabled()){
                                    endTime_text.setText(String.format("%02d:%02d", minutes, seconds));
                                }else {
                                    return ;
                                }

                            }
                        }
                    });



                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void PostSummaryResult(int startTime, int endTime) throws IOException {
        Log.d("postsu", "here");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30,TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://34.124.231.72:8000/summarization/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SummaryRetrofitService summaryService = retrofit.create(SummaryRetrofitService.class);


        File file = new File(Environment.getExternalStorageDirectory() + "/file.txt");
        Log.d("path",Environment.getExternalStorageDirectory() + "/file.txt" );
        Path filePath = Paths.get(file.getPath());

        try {
            Files.deleteIfExists(filePath);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);

            for(int i=0; i< SubActivity.sttList.size();i++) {
                int sttTime = (int) (Double.parseDouble(SubActivity.sttList.get(i).split(" ")[0]) * 1000);
                if((startTime * 1000 <=  sttTime )&& (endTime * 1000 >= sttTime)) {
                    Log.d("write", SubActivity.sttList.get(i).split(":")[1]);
                    writer.write(SubActivity.sttList.get(i).split(":")[1]);
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part filePart = null;
        try {
            filePart = MultipartBody.Part.createFormData("file", URLEncoder.encode(file.getName(), "utf-8"), fileBody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<SummaryPostResult> call = summaryService.request(filePart);
        call.enqueue(new Callback<SummaryPostResult>() {
            @Override
            public void onResponse(Call<SummaryPostResult> call, Response<SummaryPostResult> response) {
                Log.d("respppppppppppppppppppse", "ere");
                if(response.isSuccessful()) {
                    Log.d("server", "start");
                    SummaryPostResult result = response.body();
                    String summary_content = result.getResult();

                    String summary_name = startTime_text.getText() + " - " + endTime_text.getText();

                    String sql = "INSERT INTO summaryTable('summary_name','summary_context') values('" + summary_name + "','" + summary_content + "');";
                    summaryDB.execSQL(sql);
                    Log.d("server", "success");
                }else {
                    Log.d("fail", "fail");
                }
            }

            @Override
            public void onFailure(Call<SummaryPostResult> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });

    }



}