package com.example.sanghyunj.speckerapp.controller;

/**
 * Created by sanghyunj on 09/06/2017.
 */

public interface Publisher {
    void friendListUpdate();
    void addFriend(String key);

    void searchedListUpdate(String keyword);


    void addFriendListObserver(FriendListObserver o);
    void addSearchedListObserver(SearchedListObserver o);

    void deleteFriendListObserver(FriendListObserver o);
    void deleteSearchedListObserver(FriendListObserver o);

    void notifyFriendListObserver();
    void notifySearchedListObserver();


}
