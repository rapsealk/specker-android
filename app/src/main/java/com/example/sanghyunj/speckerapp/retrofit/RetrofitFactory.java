package com.example.sanghyunj.speckerapp.retrofit;

import com.example.sanghyunj.speckerapp.retrofit.Body.AddMarker;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by rapsealk on 2017. 10. 26..
 */

public interface RetrofitFactory {

    @POST("addMarker")
    Call<DefaultResponse> addMarker(
            @Header("Authorization") String authorization,
            @Body AddMarker body
    );

    public static final Retrofit instance = new Retrofit.Builder()
            .baseUrl("http://52.78.4.96:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
