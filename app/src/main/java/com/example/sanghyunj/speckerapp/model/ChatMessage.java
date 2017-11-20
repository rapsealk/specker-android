package com.example.sanghyunj.speckerapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lineplus on 2017. 6. 5..
 */

public class ChatMessage {
    private String uid;
    private String text;
    private String name;
    private String photoUrl;
    private String imageUrl;
    private String date;
    private int type = 1;
    private String id;

    public ChatMessage() {
    }

    public ChatMessage( String text, String name, String photoUrl, String imageUrl) {
        this.uid = uid;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        this.date = sdfNow.format(date);

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
