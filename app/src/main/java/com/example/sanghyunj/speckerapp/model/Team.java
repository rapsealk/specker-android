package com.example.sanghyunj.speckerapp.model;

import com.example.sanghyunj.speckerapp.util.GlobalVariable;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public class Team implements Element {

    private String name;
    private String title;
    private String profile;

    public String getProfile() {
        return profile;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUid() {
        return null;
    }


    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public int getType() {
        return GlobalVariable.TEAM;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}
