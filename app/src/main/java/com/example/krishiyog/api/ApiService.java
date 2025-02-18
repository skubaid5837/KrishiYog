package com.example.krishiyog.api;
import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("v1/chat/completions")
    Call<GPTResponse> detectDisease(
            @Header("Authorization") String authorization,
            @Body GPTRequest request
    );

}