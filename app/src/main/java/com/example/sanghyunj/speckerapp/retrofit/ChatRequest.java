package com.example.sanghyunj.speckerapp.retrofit;

import com.example.sanghyunj.speckerapp.retrofit.Body.ChatroomMetaResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by rapsealk on 2017. 11. 3..
 */

public interface ChatRequest {

    /*
    @POST("getChatrooms")
    Observable<ChatroomMetaResponse> getChatrooms(
        @Header("Authentication") String authentication
    );
    */
    @POST("getChatrooms")
    Call<ChatroomMetaResponse> getChatrooms(
        @Header("Authorization") String authorization
    );
    /*
    @POST("getChat")
    Observable<GetChatResponse> getChat(
        @Header("Authorization") String authorization,
        @Body GetChatBody body
    );
    */
    @POST("getChat")
    Call<GetChatResponse> getChat(
        @Header("Authorization") String authorization,
        @Body GetChatBody body
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.4.96:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}