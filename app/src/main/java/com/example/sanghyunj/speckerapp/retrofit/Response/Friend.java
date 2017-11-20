package com.example.sanghyunj.speckerapp.retrofit.Response;

import com.example.sanghyunj.speckerapp.model.Element;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public class Friend implements Element {

    public String _id;
    public String name;
    public String gravatar;

    public Friend(String _id, String name, String gravatar) {
        this._id = _id;
        this.name = name;
        this.gravatar = gravatar;
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getType() { return 0; }

    @Override
    public String getTitle() { return ""; }

    @Override
    public String getProfile() { return gravatar; }

    @Override
    public String getUid() { return ""; }

    @Override
    public String getId() { return _id; }

    @Override
    public String toString() {
        return "{ _id: " + _id + ", name: " + name + ", gravatar: " + gravatar + " }";
    }
}
