package com.example.android;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecordFragment extends Fragment {

    View view;

    RecyclerView sttRecyclerView;

    public static STTAdapter sttAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_record, container, false);
        sttRecyclerView = (RecyclerView) view.findViewById(R.id.sttRecyclerView);
        sttAdapter = new STTAdapter(getActivity().getApplicationContext(), SubActivity.sttList);
        sttRecyclerView.setAdapter(sttAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sttRecyclerView.setLayoutManager(mLayoutManager);

        sttAdapter.setOnSttClickListener(new STTAdapter.OnSttClickListener() {
            @Override
            public void onSttClick(int position, double time) {
                Log.d("isplay", String.valueOf(SubActivity.isPlaying));
                if(SubActivity.isPlaying == true) {
                    stopAudio();
                    SubActivity.mediaPlayer = null;
                    SubActivity.mediaPlayer = new MediaPlayer();
                    File file = new File(SubActivity.uriName);
                    try {
                        SubActivity.mediaPlayer.setDataSource(file.getAbsolutePath());
                        SubActivity.mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                SubActivity.isPlaying = true;
                Log.d("time", String.valueOf(time * 1000));
                SubActivity.mediaPlayer.seekTo((int)(time * 1000));
                SubActivity.mediaPlayer.start();
            }
        });
        Thread();

        SubActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        return view;
    }

    public void Thread(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(SubActivity.isFinish || !SubActivity.fragmentMode.equals("RecordFragment")){
                        return ;
                    }
                    Log.d("chag", "run");
                    if(SubActivity.isPlaying){
                        for(int i=0; i< sttAdapter.sttModels.size();i++){
                            if(SubActivity.isPlaying == true) {
                                if ((int) (Double.parseDouble(sttAdapter.sttModels.get(i).split(" ")[0]) * 1000) <= SubActivity.mediaPlayer.getCurrentPosition() &&
                                        (int) (Double.parseDouble(sttAdapter.sttModels.get(i + 1).split(" ")[0]) * 1000) > SubActivity.mediaPlayer.getCurrentPosition()){
                                    //notifyItemChanged(i);
                                    Log.d("cj","ere");
                                    int finalI = i;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sttAdapter.notifyDataSetChanged();
                                            sttRecyclerView.scrollToPosition(finalI);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                        try {
                            Thread.sleep(500);
                            Log.d("th", "sleep");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void stopAudio(){
        SubActivity.mediaPlayer.stop();
        SubActivity.mediaPlayer.reset();
        SubActivity.mediaPlayer.release();

        SubActivity.isPlaying = false;
    }

    public void refreshList(){
        Log.d("he", String.valueOf(SubActivity.sttList.size()));
        sttAdapter.updateList(SubActivity.sttList);
    }


}