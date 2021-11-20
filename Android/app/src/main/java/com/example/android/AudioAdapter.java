package com.example.android;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
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

    private OnNameClickListener nameClickListener = null;

    private OnReplayClickListener replayClickListner = null;

    private OnTrashClickListener trashClickListener = null;

    private OnSeekBarChangeListener seekBarChangeListener = null;

    private OnExpandClickListener expandClickListener = null;


    public interface OnIconClickListener {
        void onItemClick(View view, int position, SeekBar seekBar, TextView currenttimeText);
    }

    public interface OnNameClickListener {
        void onNameClick(View view, int position);
    }

    public interface OnReplayClickListener {
        void onReplayClick(View view, int position);
    }

    public interface OnTrashClickListener {
        void onTrashClick(View view, int position);
    }

    public interface OnSeekBarChangeListener {
        void onSeekBarChange(int position, SeekBar seekbar, int progress, TextView currenttimeText);
    }

    public interface OnExpandClickListener {
        void onExpandClick(int position, TextView currenttimeText, TextView endtimeText);
    }

    public void setOnItemClickListener(OnIconClickListener audioPlayListener){
        this.audioPlayListener = audioPlayListener;
    }

    public void setOnNameClickListener(OnNameClickListener nameClickListener){
        this.nameClickListener = nameClickListener;
    }

    public void setOnReplayClickListener(OnReplayClickListener replayClickListner) {
        this.replayClickListner = replayClickListner;
    }

    public void setOnTrashClickListener(OnTrashClickListener trashClickListener){
        this.trashClickListener = trashClickListener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener seekBarChangeListener) {
        this.seekBarChangeListener = seekBarChangeListener;
    }

    public void setOnExpandClickListener(OnExpandClickListener expandClickListener){
        this.expandClickListener = expandClickListener;
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
        //Log.d("here",file.getName());
        recordViewHolder.audioTitle.setText(file.getName());
        MediaPlayer mediaPlayer = new MediaPlayer();


        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int time = mediaPlayer.getDuration();
        time /= 1000;
        int minutes = time / 60;
        int seconds = time % 60;
        recordViewHolder.endtimeText.setText(String.format("%02d:%02d", minutes,seconds));

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

        ImageButton replay_button;
        ImageButton play_button;
        ImageButton trash_button;

        TextView currenttimeText;
        TextView endtimeText;

        SeekBar seekBar;
        LinearLayout playerView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audioTitle);

            expand_button = itemView.findViewById(R.id.expand_button);

            playerView = itemView.findViewById(R.id.playerView);

            replay_button = itemView.findViewById(R.id.replay_button);
            play_button = itemView.findViewById(R.id.play_button);
            trash_button = itemView.findViewById(R.id.trash_button);

            seekBar = itemView.findViewById(R.id.seekBar);

            currenttimeText = itemView.findViewById(R.id.currenttimeText);
            endtimeText = itemView.findViewById(R.id.endtimeText);


            Log.d("a", String.valueOf(getAdapterPosition()));
//            File file = new File(String.valueOf(MainActivity.audioList.get(getAdapterPosition())));
//            MediaPlayer mediaPlayer = new MediaPlayer();
//
//
//            try {
//                mediaPlayer.setDataSource(file.getAbsolutePath());
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            //Log.d("pos", String.valueOf(pos));
//            //Log.d("pos", String.valueOf(endtimeText));
//            endtimeText.setText(String.valueOf(mediaPlayer.getDuration()));

            replay_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(replayClickListner != null) {
                            replayClickListner.onReplayClick(v, pos);
                        }
                    }
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            if(seekBarChangeListener != null) {
                                seekBarChangeListener.onSeekBarChange(pos, seekBar, progress, currenttimeText);
                            }
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
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //play 음악 실행
                    //pos -> list순서
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(audioPlayListener != null) {
                            audioPlayListener.onItemClick(v, pos, seekBar, currenttimeText);
                        }
                    }
                }
            });

            audioTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(nameClickListener != null) {
                            nameClickListener.onNameClick(v, pos);
                        }
                    }
                    Intent intent = new Intent(context.getApplicationContext(), SubActivity.class);
                }
            });

            trash_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(trashClickListener != null) {
                            trashClickListener.onTrashClick(v, pos);
                        }
                    }

                }
            });
            expand_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

//                    TextView end = (TextView) itemView.findViewById(R.id.endtimeText);
//
//                    Log.d("end", String.valueOf(end));
//                    Log.d("endtimeText", String.valueOf(end));
//
//                    Uri uriName = Uri.parse(MainActivity.getUriName(pos));
//
//                    File file = new File(String.valueOf(uriName));
//
//                    MediaPlayer mediaPlayer = new MediaPlayer();
//
//
//                    try {
//                        mediaPlayer.setDataSource(file.getAbsolutePath());
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    //Log.d("pos", String.valueOf(pos));
//                    //Log.d("pos", String.valueOf(endtimeText));
//                    endtimeText.setText(String.valueOf(mediaPlayer.getDuration()));

                    if(selectedItems.get(pos)) {
                        selectedItems.delete(pos);
                        Log.d("here", "del");
                        //endtimeText.setText(String.valueOf(mediaPlayer.getDuration()));

                    } else {
                        selectedItems.delete(prePosition);
                        selectedItems.put(pos, true);
                        Log.d("here","put");
                        //endtimeText.setText(String.valueOf(mediaPlayer.getDuration()));

                    }
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(pos);

                    prePosition = pos;

//                    if(pos != RecyclerView.NO_POSITION ){
//                        if(expandClickListener != null ){
//                            Log.d("pos", String.valueOf(pos));
//                            expandClickListener.onExpandClick(pos, currenttimeText, endtimeText);
//                        }
//
//                    }




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
