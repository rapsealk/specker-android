package com.example.sanghyunj.speckerapp.retrofit;

import com.example.sanghyunj.speckerapp.retrofit.Body.AddFriendBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.CreateChatroomBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.GetFriendListBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.RemoveChatroomBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.RemoveFriendBody;
import com.example.sanghyunj.speckerapp.retrofit.Body.SearchUserBody;
import com.example.sanghyunj.speckerapp.retrofit.Response.GetFriendsListResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.GetMarkerResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.ResponseWithObjectId;
import com.example.sanghyunj.speckerapp.retrofit.Response.SearchUserResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public interface Api {

    @POST("signUp")
    Call<DefaultResponse> signUp(
            @Header("Authorization") String authorization,
            @Body SignUpUser body
    );

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

    @POST("removeFriend")
    Call<DefaultResponse> removeFriend(
        @Header("Authorization") String authorization,
        @Body RemoveFriendBody body
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

    @POST("removeChatroom")
    Call<DefaultResponse> removeChatroom(
        @Header("Authorization") String authorization,
        @Body RemoveChatroomBody body
    );

    @POST("removeChatroom")
    Observable<DefaultResponse> removeChatroomRx(
        @Header("Authorization") String authorization,
        @Body RemoveChatroomBody body
    );

    @GET("getMarker")
    Observable<GetMarkerResponse> getMarker(
        @Header("Authorization") String authorization,
        @Query("latitudeA") double latitudeA,
        @Query("longitudeA") double longitudeA,
        @Query("latitudeB") double latitudeB,
        @Query("longitudeB") double longitudeB
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.4.96:3000/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}