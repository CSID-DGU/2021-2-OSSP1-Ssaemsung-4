package com.example.android;

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


public class BookMarkFragment extends Fragment {

    STTAdapter sttAdapter;
    View view;

    ArrayList<Integer> bookmarkTimeList;
    ArrayList<String> bookmarkSttList;

    BookMarkDBHelper bookMarkDBHelper;
    SQLiteDatabase bookmarkDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bookMarkDBHelper = new BookMarkDBHelper(getActivity().getApplicationContext(), "bookmarkDatabase.db", null, 1);
        bookmarkDB = bookMarkDBHelper.getWritableDatabase();
        bookMarkDBHelper.onCreate(bookmarkDB);

        //bookmarkdb 시간 뽑기
        Cursor cursor = bookmarkDB.rawQuery("SELECT time FROM bookmarkTable WHERE record_name='" + SubActivity.uriName +"'", null);

        bookmarkTimeList = new ArrayList<>();

        while(cursor.moveToNext()){
            int time = cursor.getInt(0);
            bookmarkTimeList.add(time);
        }

        bookmarkSttList = new ArrayList<>();

        //stt리스트 시간뽑기
        for(int i=0; i<bookmarkTimeList.size(); i++){
            for(int j=0; j<SubActivity.copysttList.size(); j++){
                int sttTime = (int)Double.parseDouble(SubActivity.copysttList.get(j).split(" ")[0]);
                int sttNextTime = (int)Double.parseDouble(SubActivity.copysttList.get(j + 1).split(" ")[0]);
                if(sttTime <= bookmarkTimeList.get(i) && bookmarkTimeList.get(i) < sttNextTime ){
                    bookmarkSttList.add(SubActivity.copysttList.get(j));
                    break;
                }
            }
        }

        view = inflater.inflate(R.layout.fragment_record, container, false);
        RecyclerView sttRecyclerView = (RecyclerView) view.findViewById(R.id.sttRecyclerView);
        sttAdapter = new STTAdapter(getActivity().getApplicationContext(), bookmarkSttList);
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
        if(SubActivity.mediaPlayer != null) {
            SubActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopAudio();
                }
            });
        }

        return view;
    }

    private void stopAudio(){
        SubActivity.mediaPlayer.stop();
        SubActivity.mediaPlayer.reset();
        SubActivity.mediaPlayer.release();

        SubActivity.isPlaying = false;
    }
}