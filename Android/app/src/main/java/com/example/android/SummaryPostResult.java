package com.example.android;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class SummaryPostResult {
    @SerializedName("result")
    private String result;

    public String getResult(){ return result; }



    @NonNull
    @Override
    public String toString() {
        return "PostResult{" +
                "result" + result +
                "}";
    }
}
