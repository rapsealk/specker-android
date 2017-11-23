package com.example.sanghyunj.speckerapp.retrofit.Response;

/**
 * Created by rapsealk on 2017. 11. 23..
 */

public class ResponseWithObjectId {

    public String result;
    public String _id;

    @Override
    public String toString() {
        return "{ result: " + result + ", _id: " + _id + " }";
    }
}
