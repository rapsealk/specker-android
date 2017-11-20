package com.example.sanghyunj.speckerapp.retrofit;

/**
 * Created by rapsealk on 2017. 11. 8..
 */

public class GetChatBody {

    public String uid;
    public String chatroom;
    public long timestamp;

    public GetChatBody(String uid, String chatroom, long timestamp) {
        this.uid = uid;
        this.chatroom = chatroom;
        this.timestamp = timestamp;
    }
}
