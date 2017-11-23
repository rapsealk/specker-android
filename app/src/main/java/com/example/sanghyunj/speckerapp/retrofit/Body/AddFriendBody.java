package com.example.sanghyunj.speckerapp.retrofit.Body;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public class AddFriendBody {

    public String friend;
    public long timestamp;

    public AddFriendBody(String friend, long timestamp) {
        this.friend = friend;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{ friend: " + friend + " }";
    }
}
