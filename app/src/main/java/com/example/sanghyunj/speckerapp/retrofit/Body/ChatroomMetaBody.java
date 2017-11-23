package com.example.sanghyunj.speckerapp.retrofit.Body;

/**
 * Created by rapsealk on 2017. 11. 3..
 */

public class ChatroomMetaBody {
    public String _id = "";
    public int participants = 0;
    public String lastChat = "";
    public long lastTimestamp = 0;

    public ChatroomMetaBody(String _id, int participants, String lastChat, long lastTimestamp) {
        this._id = _id;
        this.participants = participants;
        this.lastChat = lastChat;
        this.lastTimestamp = lastTimestamp;
    }
}