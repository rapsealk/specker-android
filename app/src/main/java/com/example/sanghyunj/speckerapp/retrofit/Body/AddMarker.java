package com.example.sanghyunj.speckerapp.retrofit.Body;

/**
 * Created by rapsealk on 2017. 10. 26..
 */

public class AddMarker {

    public String title;
    public double latitude;
    public double longitude;

    public AddMarker(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
