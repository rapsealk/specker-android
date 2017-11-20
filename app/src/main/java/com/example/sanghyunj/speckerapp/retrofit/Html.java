package com.example.sanghyunj.speckerapp.retrofit;

import java.io.Serializable;

/**
 * Created by rapsealk on 2017. 10. 9..
 */

public class Html implements Serializable {
    public String markup;

    public Html(String markup) {
        this.markup = markup;
    }

    @Override public String toString() {
        return "{ \"markup\": \"" + markup + "\" }";
    }
}
