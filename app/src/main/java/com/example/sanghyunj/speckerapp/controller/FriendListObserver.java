package com.example.sanghyunj.speckerapp.controller;

import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;

import java.util.List;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public interface FriendListObserver {
    void update(List<FriendListItem> friendListItems);
}
