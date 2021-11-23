package com.example.android;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {

    @Multipart
    @POST("http://34.64.68.234:8080/api/chgSphToTxt/")
    Call<STTPostResult> request(@Part MultipartBody.Part file);

//    String path = "/storage/emulated/0/Android/data/com.example.android/files/토플 지문.wav";
//
//    File file = new File(path);
//    RequestBody fileBody = RequestBody.create(MediaType.parse("audio/*"), file);
//
//    //서버에서 받는 키값,파일 이름 string, request body 객체
//    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", path, fileBody);
}
