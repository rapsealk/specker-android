package com.example.sanghyunj.speckerapp.model.FriendList;

import com.example.sanghyunj.speckerapp.model.Element;


/**
 * Created by sanghyunj on 09/06/2017.
 */

public class FriendListItem {
    private boolean isBookmark;
    private Element element;
    private int type;

    public FriendListItem(Element element) {
        this.element = element;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public String getId() {
        return element.getId();
    }

    public String getUid() {
        return element.getUid();
    }

    public String getName() {
        return element.getName();
    }

    public String getStatusMessage() {
        return element.getTitle();
    }

    public String getProfileImage() {
        return element.getProfile();
    }

}
