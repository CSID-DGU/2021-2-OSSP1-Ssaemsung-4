package com.example.android;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class STTAdapter extends RecyclerView.Adapter{

    ArrayList<String> sttModels;
    Context context;

    Handler handler;

    private OnSttClickListener sttClickListener = null;

    public interface OnSttClickListener {
        void onSttClick(int position, double time);
    }

    public void setOnSttClickListener(OnSttClickListener sttClickListener){
        this.sttClickListener = sttClickListener;
    }

    public STTAdapter(Context context, ArrayList<String> sttModels){
        this.sttModels = sttModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stt,parent,false);
        STTViewHolder viewHolder = new STTViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("bind", "bind");
        STTViewHolder sttViewHolder = (STTViewHolder) holder;
        //"1.8 Sec - Speaker 3: test for part 1 passage 1 listen to a conversation between a student and her professor"
        String stt = sttModels.get(position);
        sttViewHolder.speakerText.setText(stt.split(":")[0].split("-")[1]);
        int time = (int)Double.parseDouble(stt.split(" ")[0]);
        int minutes = time / 60;
        int seconds = time % 60;
        sttViewHolder.timeText.setText((String.format("%02d:%02d", minutes,seconds)));
        sttViewHolder.msgText.setText(stt.split(":")[1]);
        if(SubActivity.isPlaying == true) {
            if ((int) (Double.parseDouble(sttModels.get(position).split(" ")[0]) * 1000) <= SubActivity.mediaPlayer.getCurrentPosition() &&
                    (int) (Double.parseDouble(sttModels.get(position + 1).split(" ")[0]) * 1000) > SubActivity.mediaPlayer.getCurrentPosition()) {
                sttViewHolder.sttView.setBackgroundColor(Color.GRAY);
            } else {
                sttViewHolder.sttView.setBackgroundColor(Color.WHITE);
            }
        }

    }


    @Override
    public int getItemCount() {
        return sttModels.size();
    }

    public void updateList(ArrayList<String> sttModels){
        this.sttModels = sttModels;
        this.notifyDataSetChanged();
    }

//    public void changeTextColor(){
//
//    }

    public class STTViewHolder extends RecyclerView.ViewHolder{

        LinearLayout sttView;

        TextView speakerText;
        TextView timeText;

        TextView msgText;

        public STTViewHolder(@NonNull View itemView) {
            super(itemView);

            sttView = itemView.findViewById(R.id.sttView);

            speakerText = itemView.findViewById(R.id.speakerText);
            timeText = itemView.findViewById(R.id.timeText);

            msgText = itemView.findViewById(R.id.msgText);

            sttView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String stt = sttModels.get(pos);
                    double time = Double.parseDouble(stt.split(" ")[0]);
                    if(pos != RecyclerView.NO_POSITION) {
                        if(sttClickListener != null) {
                            sttClickListener.onSttClick(pos,time);
                        }
                    }

                }
            });
        }
    }

//    private Runnable remarkStt(){
//        Runnable task = new Runnable() {
//            @Override
//            public void run() {
//                if(handler == null){
//                    return ;
//                }
//                //Log.d("run","here");
//                long currentTimeStamp = SystemClock.elapsedRealtime();
//                countTimeSeconds = (int)((currentTimeStamp - startTimeStamp )/1000L);
//                updateCountTime(countTimeSeconds);
//                handler.postDelayed(this, 1000);
//            }
//        };
//
//        return task;
//    }


}
