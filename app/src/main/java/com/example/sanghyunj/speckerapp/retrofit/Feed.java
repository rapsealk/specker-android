package com.example.sanghyunj.speckerapp.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rapsealk on 2017. 9. 21..
 */

public class Feed {

    public List<FeedData> data = new ArrayList<>();
    public String nextIndex;

    @Override
    public String toString() {
        String string = "{ data: [";
        for (FeedData feedData: data) {
            string += "\n\t{\n";
            string += "\t\t\"_id\": " + feedData._id + ",\n";
            string += "\t\t\"content\": " + feedData.content + ",\n";
            /*
            string += "\t\t\"comment\": [";
            for (String comm: feedData.comment) {
                string += "\n\t\t\t\"" + comm + "\",\n";
            }
            string += "\t\t\"title\": " + feedData.title + ",\n";
            string += "\t\t\"mention\": " + feedData._id + ",\n";
            string += "\t\t\"tag\": " + feedData.tag + ",\n";
            */
            string += "\t\t\"date\": " + feedData.date + ",\n";
            /*
            string += "\t\t\"view\": " + feedData._id + ",\n";
            string += "\t\t\"popular\": " + feedData._id + ",\n";
            */
            string += "\t\t\"thumb\": " + feedData.thumb + ",\n";
            string += "\t\t\"user\": " + feedData.user + ",\n";

            string += " }\n";
        }
        string += " ],\n\t\"nextIndex\": \""+nextIndex+"\" }";
        return string;
    }

}

class Popular {
    public List<String> block;
    public List<String> unlike;
    public List<String> like;
}

class Thumb {
    public String title;
    public String content;
    public String img;

    @Override
    public String toString() {
        return "{ \"title\": \""+title+"\", \"content\": \""+content+"\", \"img\": \""+img+"\" }";
    }
}