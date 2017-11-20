package com.example.sanghyunj.speckerapp.retrofit;

import java.util.List;

/**
 * Created by rapsealk on 2017. 10. 8..
 */

public class FeedData {

    public String _id;
    public String content;
    public List<String> comment;
    public String title;
    public List<String> mention;
    public List<String> tag;
    public String date; // -> DateTime
    public List<String> view;
    public Popular popular;
    public Thumb thumb;
    public User user;

}