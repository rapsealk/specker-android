package com.example.sanghyunj.speckerapp.retrofit.Body;

/**
 * Created by rapsealk on 2017. 11. 26..
 */

public class RemoveFriendBody {

    public String friend;

    public RemoveFriendBody(String friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "{ friend: " + friend + " }";
    }
}
