package com.example.sanghyunj.speckerapp.model.SearchedList;

import com.example.sanghyunj.speckerapp.model.Element;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public class SearchedListItem{
    private boolean isKnow;
    private Element element;


    public SearchedListItem(boolean isKnow, Element element) {
        this.isKnow = isKnow;
        this.element = element;
    }

    public boolean isKnow() {
        return isKnow;
    }

    public void setKnow(boolean know) {
        isKnow = know;
    }

    public Element getElement() {
        return element;
    }

    public int getType(){
        return element.getType();
    }


    public String getName() {
        return element.getName();
    }

    public String getTitle() {
        return element.getTitle();
    }

    public String getProfile() {
        return element.getProfile();
    }

    public String getId() {
        return element.getId();
    }

    public String getUid() {
        return element.getUid();
    }

}
