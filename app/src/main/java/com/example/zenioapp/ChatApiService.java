package com.example.zenioapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApiService {
    @POST("models/{model}")
    Call<List<ChatResponse>> getChatResponse(
            @Path("model") String model,
            @Header("Authorization") String authHeader,
            @Body ChatRequest request
    );
}