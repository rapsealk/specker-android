package com.example.sanghyunj.speckerapp.model;

import com.example.sanghyunj.speckerapp.util.GlobalVariable;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public class Group implements Element {


    @Override
    public int getType() {
        return GlobalVariable.GROUP;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getProfile() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUid() {
        return null;
    }

}
