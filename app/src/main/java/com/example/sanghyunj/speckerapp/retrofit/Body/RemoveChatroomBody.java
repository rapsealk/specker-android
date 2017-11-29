package com.example.sanghyunj.speckerapp.retrofit.Body;

/**
 * Created by rapsealk on 2017. 11. 27..
 */

public class RemoveChatroomBody {

    private String chatroom;

    public RemoveChatroomBody(String chatroom) {
        this.chatroom = chatroom;
    }

    public String getChatroom() {
        return chatroom;
    }
}
