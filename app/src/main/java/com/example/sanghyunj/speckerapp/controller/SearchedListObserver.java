package com.example.sanghyunj.speckerapp.controller;

import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;
import com.example.sanghyunj.speckerapp.model.SearchedList.SearchedListItem;

import java.util.List;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public interface SearchedListObserver {
    void update(List<SearchedListItem> searchedListItemList);
}
