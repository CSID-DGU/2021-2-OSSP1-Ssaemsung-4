package com.example.android;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SummaryRetrofitService {

    @Multipart
    @POST("http://34.124.231.72:8000/summarization/")
    Call<SummaryPostResult> request(@Part MultipartBody.Part file);

}
