package com.example.sanghyunj.speckerapp.retrofit.Body;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public class AddFriendBody {

    public String friend;

    public AddFriendBody(String friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "{ friend: " + friend + " }";
    }
}
