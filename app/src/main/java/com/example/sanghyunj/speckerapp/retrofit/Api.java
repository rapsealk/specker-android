package com.example.sanghyunj.speckerapp.retrofit;

import com.example.sanghyunj.speckerapp.retrofit.Body.AddFriendBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.CreateChatroomBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.GetFriendListBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.SearchUserBody;
import com.example.sanghyunj.speckerapp.retrofit.Response.GetFriendsListResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.ResponseWithObjectId;
import com.example.sanghyunj.speckerapp.retrofit.Response.SearchUserResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public interface Api {

    @POST("searchUser")
    Call<SearchUserResponse> searchUser(
        @Header("Authorization") String authorization,
        @Body SearchUserBody body
    );

    @POST("addFriend")
    Call<DefaultResponse> addFriend(
        @Header("Authorization") String authorization,
        @Body AddFriendBody body
    );

    @POST("getFriendsList")
    Call<GetFriendsListResponse> getFriendsList(
        @Header("Authorization") String authorization,
        @Body GetFriendListBody body
    );

    @POST("createChat")
    Call<ResponseWithObjectId> createChatroom(
        @Header("Authorization") String authorization,
        @Body CreateChatroomBody body
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.4.96:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
