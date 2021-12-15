package com.example.android;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SummaryPostResult {
    @SerializedName("result")
    private List<String> result;

    public List<String> getResult(){ return result; }



    @NonNull
    @Override
    public String toString() {
        return "PostResult{" +
                "result" + result +
                "}";
    }
}
