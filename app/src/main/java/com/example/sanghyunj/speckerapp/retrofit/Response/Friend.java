package com.example.sanghyunj.speckerapp.retrofit.Response;

import com.example.sanghyunj.speckerapp.model.Element;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public class Friend implements Element {

    public String uid;
    public String name;
    public String gravatar;
    public long timestamp;

    public Friend(String uid, String name, String gravatar, long timestamp) {
        this.uid = uid;
        this.name = name;
        this.gravatar = gravatar;
        this.timestamp = timestamp;
    }

    public long getTimestamp() { return timestamp; }

    @Override
    public String getName() { return name; }

    @Override
    public int getType() { return 0; }

    @Override
    public String getTitle() { return ""; }

    @Override
    public String getProfile() { return gravatar; }

    @Override
    public String getUid() { return uid; }

    @Override
    public String getId() { return ""; }

    @Override
    public String toString() {
        return "{ uid: " + uid + ", name: " + name + ", gravatar: " + gravatar + " }";
    }
}
