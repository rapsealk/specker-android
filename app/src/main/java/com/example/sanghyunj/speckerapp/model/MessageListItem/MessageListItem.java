package com.example.sanghyunj.speckerapp.model.MessageListItem;

/**
 * Created by lineplus on 2017. 6. 14..
 */

public class MessageListItem {
    private String uid;
    private String userName;
    private String photoUrl;
    private String text;
    private int type;

    public MessageListItem(String uid, String userName, String photoUrl, String text, int type) {
        this.uid = uid;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.text = text;
        this.type = type;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
