package com.example.android;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class STTPostResult {

    @SerializedName("start_time")
    private String[] start_time;

    @SerializedName("msg_log")
    private String[] msg_log;

    @SerializedName("text")
    private String[] text;

    @SerializedName("speaker_no")
    private String[] speaker_no;

    public String[] getStart_time(){ return start_time; }

    public String[] getMsg_log() { return  msg_log; }

    public String[] getText(){
        return text;
    }

    public String[] getSpeaker_no(){ return speaker_no; }


    @NonNull
    @Override
    public String toString() {
        return "PostResult{" +
                "start_time" + start_time +
                "text" + text +
                "speaker_no" + speaker_no +
                "}";
    }
}
