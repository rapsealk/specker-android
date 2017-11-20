package com.example.sanghyunj.speckerapp.controller;

import android.util.Log;

import com.example.sanghyunj.speckerapp.model.FriendList.FriendListItem;
import com.example.sanghyunj.speckerapp.model.SearchedList.SearchedListItem;
import com.example.sanghyunj.speckerapp.model.User;
import com.example.sanghyunj.speckerapp.util.GlobalVariable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Firebase implements Publisher {

    private List<FriendListItem> friendListItemList;
    private List<SearchedListItem> searchedListItemList;


    private static Firebase instance;
    private ArrayList<FriendListObserver> friendListObservers;
    private ArrayList<SearchedListObserver> searchedListObservers;


    private Firebase(){
        friendListObservers = new ArrayList<>();
        searchedListObservers = new ArrayList<>();

        initFriendList();
        initSearchedList();
    }

    public static Firebase getInstance () {
        if ( instance == null )
            instance = new Firebase();
        return instance;
    }

    public void initFriendList(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        friendListItemList = new ArrayList<>();

        User friendListUser = new User();

        friendListUser.setEmail(firebaseUser.getEmail());
        friendListUser.setFcmToken(FirebaseInstanceId.getInstance().getToken());
        friendListUser.setName(firebaseUser.getDisplayName());
        friendListUser.setProfile(firebaseUser.getPhotoUrl().toString());
        friendListUser.setUid(firebaseUser.getUid());

        FriendListItem friendListItem = new FriendListItem(friendListUser);
        friendListItem.setType(GlobalVariable.FRIEND_LIST_PROFILE_ME);
        friendListItemList.add(friendListItem);
        friendListUpdate();


    }

    private void initSearchedList(){
        searchedListItemList = new ArrayList<>();
    }

    @Override
    public void friendListUpdate() {

        FirebaseDatabase.getInstance().getReference("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalData = (int)dataSnapshot.getChildrenCount();
                for(DataSnapshot d : dataSnapshot.getChildren()){

                    String key = d.getValue().toString();
                    FirebaseDatabase.getInstance().getReference("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User friendListUser = dataSnapshot.getValue(User.class);

                            count++;

                            FriendListItem friendListItem = new FriendListItem(friendListUser);
                            friendListItem.setType(GlobalVariable.FRIEND_LIST_PROFILE_FRIEND);
                            friendListItemList.add(friendListItem);

                            if(count == totalData){
                                notifyFriendListObserver();
                                count = 0;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void addFriend(String key) {
        FirebaseDatabase.getInstance().getReference("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(key);
        for(SearchedListItem i : searchedListItemList){
            if(i.getUid().equals(key)){
                int index = searchedListItemList.indexOf(i);
                i.setKnow(true);
                searchedListItemList.remove(index);
                searchedListItemList.add(i);
                FriendListItem f = new FriendListItem(i.getElement());
                f.setType(GlobalVariable.FRIEND_LIST_PROFILE_FRIEND);
                friendListItemList.add(f);

            }
        }

        notifySearchedListObserver();
        notifyFriendListObserver();
    }


    @Override
    public void searchedListUpdate(String keyword) {
        searchedListItemList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalData = (int)dataSnapshot.getChildrenCount();

                for(DataSnapshot d: dataSnapshot.getChildren()) {

                    User user = d.getValue(User.class);
                    count++;
                    if (!Objects.equals(keyword, FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                            Objects.equals(keyword, user.getEmail())) {

                        FirebaseDatabase.getInstance().getReference("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                boolean isExisted = false;


                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                    if(d.getValue().equals(user.getUid()))
                                        isExisted = true;
                                    break;
                                }
                                SearchedListItem sl;
                                if(isExisted)
                                    sl = new SearchedListItem(true,user);
                                else
                                    sl = new SearchedListItem(false, user);

                                searchedListItemList.add(sl);
                                if(count == totalData){
                                    count = 0;
                                    notifySearchedListObserver();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void addFriendListObserver(FriendListObserver o) {
        friendListObservers.add(o);
    }

    @Override
    public void addSearchedListObserver(SearchedListObserver o) {
        searchedListObservers.add(o);
    }

    @Override
    public void deleteFriendListObserver(FriendListObserver o) {
        int index = friendListObservers.indexOf(o);
        friendListObservers.remove(index);
    }

    @Override
    public void deleteSearchedListObserver(FriendListObserver o) {
        int index = searchedListObservers.indexOf(o);
        searchedListObservers.remove(index);
    }

    @Override
    public void notifyFriendListObserver() {
        for(FriendListObserver o : friendListObservers){
            o.update(friendListItemList);
        }
    }

    @Override
    public void notifySearchedListObserver() {
        for(SearchedListObserver o : searchedListObservers){
            o.update(searchedListItemList);
        }
    }
}
