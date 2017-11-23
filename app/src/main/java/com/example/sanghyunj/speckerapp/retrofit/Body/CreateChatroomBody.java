package com.example.sanghyunj.speckerapp.retrofit.Body;

import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 11. 23..
 */

public class CreateChatroomBody {

    public ArrayList<String> participants;

    public CreateChatroomBody(ArrayList<String> participants) {
        this.participants = participants;
    }
}
