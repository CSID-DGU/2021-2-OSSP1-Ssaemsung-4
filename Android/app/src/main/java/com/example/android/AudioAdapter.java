package com.example.android;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

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

    //item 클릭 상태를 저장
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    //직전에 클릭됐던 item의 position
    private int prePosition = -1;


    private OnIconClickListener audioPlayListener = null;

    private OnNameClickListner nameClickListner = null;


    public interface OnIconClickListener {
        void onItemClick(View view, int position, SeekBar seekBar);
    }

    public interface OnNameClickListner {
        void onNameClick(View view, int position);
    }

    public void setOnItemClickListener(OnIconClickListener audioPlayListener){
        this.audioPlayListener = audioPlayListener;
    }

    public void setOnNameClickListener(OnNameClickListner nameClickListner){
        this.nameClickListner = nameClickListner;
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
        RecordViewHolder viewHolder = new RecordViewHolder(view);

        //생성된 뷰홀더를 리턴하여 onBindViewHolder에 전달
        return viewHolder;
    }

    @Override
    //아이템을 하나하나 보여주는 함수
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecordViewHolder recordViewHolder = (RecordViewHolder) holder;

        String uriName = String.valueOf(dataModels.get(position));
        File file = new File(uriName);
        Log.d(TAG,file.getName());
        recordViewHolder.audioTitle.setText(file.getName());

        recordViewHolder.expand_button.setImageResource(selectedItems.get(position)? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        recordViewHolder.playerView.setVisibility(selectedItems.get(position) ? View.VISIBLE : View.GONE);

        //recordViewHolder.changeVisibility(selectedItems.get(position), expand_button);

    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야함
        return dataModels.size();
    }

    //subview 세팅하는 부분
    public class RecordViewHolder extends RecyclerView.ViewHolder {
        Button audioTitle;
        ImageButton expand_button;

        ImageButton play_button;
        SeekBar seekBar;
        LinearLayout playerView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audioTitle);

            expand_button = itemView.findViewById(R.id.expand_button);

            playerView = itemView.findViewById(R.id.playerView);

            play_button = itemView.findViewById(R.id.play_button);

            seekBar = itemView.findViewById(R.id.seekBar);

            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //play 음악 실행
                    //pos -> list순서
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(audioPlayListener != null) {
                            audioPlayListener.onItemClick(v, pos, seekBar);
                        }
                    }
                }
            });

            //여기 수정해야함 -> 음악실행이 아닌 다음 뷰로 넘어가야함
            audioTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(nameClickListner != null) {
                            nameClickListner.onNameClick(v, pos);
                        }
                    }
                    Intent intent = new Intent(context.getApplicationContext(), SubActivity.class);
                }
            });
            expand_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(selectedItems.get(pos)) {
                        selectedItems.delete(pos);
                    } else {
                        selectedItems.delete(prePosition);
                        selectedItems.put(pos, true);
                    }
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(pos);

                    prePosition = pos;


                }
            });

        }


//        private void changeVisibility(final boolean isExpanded, ImageButton expand_button) {
//            //playerView 크기
//            Log.d(TAG,"")
//            int dpValue = playerView.getHeight();
//            float d = context.getResources().getDisplayMetrics().density;
//            int height = (int) (dpValue * d);
//
//            playerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//
//            expand_button.setImageResource(isExpanded? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
//
//        }

    }


}
