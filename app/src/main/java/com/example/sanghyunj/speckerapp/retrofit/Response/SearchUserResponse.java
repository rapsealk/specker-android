package com.example.sanghyunj.speckerapp.retrofit.Response;

import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 11. 15..
 */

public class SearchUserResponse {

    public String result;
    public ArrayList<SearchedUser> users;

    @Override
    public String toString() {
        return "{ result: " + result + " }";
    }
}