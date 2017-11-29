package com.example.sanghyunj.speckerapp.retrofit.Response;

import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 11. 30..
 */

public class GetMarkerResponse {

    private String result;
    private ArrayList<SPKMarker> markers = new ArrayList<>();

    public String getResult() {
        return result;
    }

    public ArrayList<SPKMarker> getMarkers() {
        return markers;
    }
}
