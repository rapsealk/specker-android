package com.example.sanghyunj.speckerapp.retrofit;

/**
 * Created by rapsealk on 2017. 10. 9..
 */

public class User {
    public String _id;
    public String name;
    public String gravatar;

    @Override
    public String toString() {
        return "{ \"_id\": \"" + _id + "\", \"name\": \"" + name + "\", \"gravatar\": \"" + gravatar + "\" }";
    }

    public String getName() { return name; }
}