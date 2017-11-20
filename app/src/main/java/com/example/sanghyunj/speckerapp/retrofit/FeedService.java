package com.example.sanghyunj.speckerapp.retrofit;

import com.example.sanghyunj.speckerapp.retrofit.Body.GetHomeFeed;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by rapsealk on 2017. 9. 21..
 */

public interface FeedService {

    @POST("getHomeFeed")
    Call<Feed> getFeeds(@Body GetHomeFeed body);

    @POST("sendHomeFeed")
    Call<DefaultResponse> sendFeed(
            @Header("Authorization") String authorization,
            @Body SendFeedData body
    );

    @Multipart
    @POST("sendImage")
    Call<DefaultResponse> sendImage(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.4.96:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
