package com.example.sanghyunj.speckerapp.retrofit.Body;

import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 11. 3..
 */

public class ChatroomMetaBody {
    public String _id = "";
    public ArrayList<String> participants = new ArrayList<>();
    // public int participants = 0;
    public String lastChat = "";
    public long lastTimestamp = 0;

    public ChatroomMetaBody(String _id, ArrayList<String> participants, String lastChat, long lastTimestamp) {
        this._id = _id;
        this.participants = participants;
        this.lastChat = lastChat;
        this.lastTimestamp = lastTimestamp;
    }
}