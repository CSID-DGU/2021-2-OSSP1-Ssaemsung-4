package com.example.android;
import com.example.android.MainActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.io.File;

public class RecordDialog {

    String TAG = "aa";
    ImageButton cancel_button;

    ImageButton update_button;
    ImageButton record_button;
    ImageButton bookmark_button;

    SoundVisualizerView soundVisualizerView;

    //CountUpView countUpView;

    TextView timeStamp_text;

    Handler handler;

    private long startTimeStamp = 0L;

    private Context context;

    Dialog recordDialog;

    public RecordDialog(Context context) {
        this.context = context;
    }

    //record dialog 생성
    public void callFunction() {

        recordDialog = new Dialog(context);


        recordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        recordDialog.setContentView(R.layout.record_dialog);
        recordDialog.getWindow().setGravity(Gravity.BOTTOM);

        recordDialog.show();

        soundVisualizerView = (SoundVisualizerView)recordDialog.findViewById(R.id.soundVisualizeView);

        timeStamp_text = (TextView) recordDialog.findViewById(R.id.timeStamp_text);

        cancel_button = (ImageButton) recordDialog.findViewById(R.id.cancel_button);

        update_button = (ImageButton) recordDialog.findViewById(R.id.update_button);
        record_button = (ImageButton) recordDialog.findViewById(R.id.record_button);
        bookmark_button = (ImageButton) recordDialog.findViewById(R.id.bookmark_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            //recordDialog view 삭제
            @Override
            public void onClick(View view) {
                soundVisualizerView.stopVisualizing();
                stopCountup();

                Uri uriName = Uri.parse(MainActivity.audioFileName);
                File file = new File(String.valueOf(uriName));
                file.delete();
                destroyDialog();
            }
        });
    }
    public void destroyDialog() {
        recordDialog.dismiss();
    }

    private Runnable countUpAction(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if(handler == null){
                    return ;
                }
                Log.d("run","here");
                long currentTimeStamp = SystemClock.elapsedRealtime();
                int countTimeSeconds = (int)((currentTimeStamp - startTimeStamp )/1000L);
                updateCountTime(countTimeSeconds);
                handler.postDelayed(this, 1000);
            }
        };

        return task;
    }

    void startCountup(){
        handler = new Handler();
        startTimeStamp = SystemClock.elapsedRealtime();
        handler.post(countUpAction());
    }

    void stopCountup(){
        Log.d("stop","stop");
        handler.removeCallbacks(countUpAction());
        handler = null;
    }

    void clearCountTime(){
        Log.d("clear","here");
        updateCountTime(0);
    }

    private void updateCountTime(int countTimeSeconds) {
        Log.d("update", String.valueOf(countTimeSeconds));
        int minutes = countTimeSeconds / 60;
        int seconds = countTimeSeconds % 60;
        timeStamp_text.setText(String.format("%02d:%02d", minutes,seconds));

    }
}