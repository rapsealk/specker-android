package com.example.sanghyunj.speckerapp.retrofit.Response;

import com.example.sanghyunj.speckerapp.model.Element;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public class SearchedUser implements Element {

    public String uid;
    public String name;
    public String email;
    public String gravatar;
    public boolean isKnown;

    @Override
    public String toString() {
        return "{ uid: " + uid + ", name: " + name + ", email: " + email + ", gravatar: " + gravatar + " }";
    }

    @Override
    public int getType() { return 0; }

    @Override
    public String getName() { return name; }

    @Override
    public String getTitle() { return ""; }

    @Override
    public String getProfile() { return gravatar; }

    @Override
    public String getId() { return email; }

    @Override
    public String getUid() { return uid; }
}
