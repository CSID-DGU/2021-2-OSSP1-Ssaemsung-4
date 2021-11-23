package com.example.android;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordFragment extends Fragment {

    View view;

    private STTAdapter sttAdapter;
    ArrayList<String> sttList;

    STTDBHelper sttDBHelper;
    SQLiteDatabase sttDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_record, container, false);

        RecyclerView sttRecyclerView = (RecyclerView) view.findViewById(R.id.sttRecyclerView);

        sttDBHelper = new STTDBHelper(getActivity().getApplicationContext(), "sttDatabase.db", null,1);
        sttDB = sttDBHelper.getWritableDatabase();
        Cursor cursor = sttDB.rawQuery("SELECT msg_log FROM sttTable WHERE record_name='" + SubActivity.uriName +"'", null);

        sttList = new ArrayList<>();

        while(cursor.moveToNext()){
            String msg_log = cursor.getString(0);
            Log.d("msg", msg_log);
            sttList.add(msg_log);
        }
        Log.d("sttlist", String.valueOf(sttList.size()));


        sttAdapter = new STTAdapter(getActivity().getApplicationContext(), sttList);
        sttRecyclerView.setAdapter(sttAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sttRecyclerView.setLayoutManager(mLayoutManager);

        sttAdapter.setOnSttClickListener(new STTAdapter.OnSttClickListener() {
            @Override
            public void onSttClick(int position, int time) {
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
                SubActivity.mediaPlayer.seekTo(time * 1000);
                SubActivity.mediaPlayer.start();
            }
        });

        SubActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });
        return view;
    }

    private void stopAudio(){
        SubActivity.mediaPlayer.stop();
        SubActivity.mediaPlayer.reset();
        SubActivity.mediaPlayer.release();

        SubActivity.isPlaying = false;
    }
}