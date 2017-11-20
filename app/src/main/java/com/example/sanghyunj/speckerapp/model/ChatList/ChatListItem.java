package com.example.sanghyunj.speckerapp.model.ChatList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lineplus on 2017. 6. 14..
 */

public class ChatListItem {

    private String roomName;
    private Map<String , String> users;
    private String rId;

    public ChatListItem(String roomName) {
        this.roomName = roomName;
        this.users = new HashMap<>();
    }


    public String getRoomName() {
        return roomName;
    }


    public Map<String, String> getUsers() {
        return users;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }
}
