package com.example.sanghyunj.speckerapp.retrofit;

import com.example.sanghyunj.speckerapp.retrofit.Body.UpdateToken;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by rapsealk on 2017. 10. 14..
 */

public interface AuthWithToken {

    @POST("signUp")
    Call<DefaultResponse> signUp(
            @Header("Authorization") String authorization,
            @Body SignUpUser body
    );

    @POST("updateToken")
    Call<DefaultResponse> updateToken(
        @Header("Authorization") String authorization,
        @Body UpdateToken body
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.4.96:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
