package com.example.sanghyunj.speckerapp.retrofit;

import java.io.Serializable;

/**
 * Created by rapsealk on 2017. 10. 9..
 */

public class SendFeedData implements Serializable {

    public Html html;

    public SendFeedData(Html html) {
        this.html = html;
    }

    @Override public String toString() {
        return "{ \"html\": \"" + html + "\" }";
    }
}