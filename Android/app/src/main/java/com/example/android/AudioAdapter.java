package com.example.android;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AudioAdapter extends RecyclerView.Adapter {

    String TAG = "aa";
    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<Uri> dataModels;
    Context context;

    private OnIconClickListener listener = null;

    public interface OnIconClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnIconClickListener listener){
        this.listener = listener;
    }

    //생성자를 통하여 데이터 리스트 context를 받음
    public AudioAdapter(Context context, ArrayList<Uri> dataModels) {
        this.dataModels = dataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio,parent, false);
        Log.d(TAG,"viewholder");
        RecordViewHolder viewHolder = new RecordViewHolder(view);

        //생성된 뷰홀더를 리턴하여 onBindViewHolder에 전달
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecordViewHolder recordViewHolder = (RecordViewHolder) holder;

        String uriName = String.valueOf(dataModels.get(position));
        File file = new File(uriName);
        Log.d(TAG,file.getName());
        recordViewHolder.audioTitle.setText(file.getName());

    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야함
        Log.d(TAG, String.valueOf(dataModels.size()));
        return dataModels.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        Button audioTitle;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audioTitle);

            audioTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(listener != null) {
                            listener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }

    }


}
