package com.example.android;

import com.example.android.MainActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class SoundVisualizerView extends View {

    private Paint amplitudePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static float LINE_WIDTH = 10f;
    private static float LINE_SPACE = 10f;

    private static float MAX_AMPLITUDE = Short.MAX_VALUE;

    private static long ACTION_INTERVAL = 20L;

    private int drawingWidth = 0;
    private int drawingHeight = 0;
    private List<Integer> drawingAmplitudes = new ArrayList<>();
    private boolean isReplaying = false;
    private int replayingPosition = 0;

//    int onRequestCurrentAmplitude = 0;


    Handler handler = new Handler();

    public SoundVisualizerView(Context context) {
        super(context);
    }

    public SoundVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SoundVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SoundVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private Runnable visualizeRepeatAction(){
        Runnable task = new Runnable() {

            @Override
            public void run() {
                if(!isReplaying) {

                    int currentAmplitude = ((MainActivity)MainActivity.mContext).getMaxAmplitude();
                    //Log.d("bb", currentAmplitude + "\n");
                    drawingAmplitudes.add(0, currentAmplitude);
                }

                invalidate();
                handler.postDelayed(this, ACTION_INTERVAL);

            }
        };

        return task;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawingWidth = w;
        drawingHeight = h;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        amplitudePaint.setColor(getContext().getColor(R.color.purple_500));
        amplitudePaint.setStrokeWidth(LINE_WIDTH);
        amplitudePaint.setStrokeCap(Paint.Cap.ROUND);

        if(canvas == null){
            return;
        }

        float centerY = drawingHeight / 2f;
        float offsetX = drawingWidth;
        int start = 0;
        if (drawingAmplitudes.size() != 0) {
            if(offsetX < drawingAmplitudes.size()){
                start = (int) offsetX;
            }

            for(int amplitude = start; amplitude<drawingAmplitudes.size(); amplitude++){
                float lineLength = drawingAmplitudes.get(amplitude) / MAX_AMPLITUDE * drawingHeight * 0.8F;

                offsetX -= LINE_SPACE;

                if(offsetX < 0) continue;

                canvas.drawLine(offsetX,centerY-lineLength / 2f, offsetX, centerY + lineLength / 2f, amplitudePaint);
            }
        }




    }


    void startVisualizing(Boolean isReplaying){
        this.isReplaying = isReplaying;
        handler.post(visualizeRepeatAction());
    }

    void stopVisualizing(){
        replayingPosition = 0;
        handler.removeCallbacks(visualizeRepeatAction());

    }

    void clearVisualization() {
        drawingAmplitudes = new ArrayList<>();
        invalidate();
    }



}
