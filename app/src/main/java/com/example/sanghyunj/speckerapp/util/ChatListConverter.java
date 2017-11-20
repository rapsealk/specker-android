package com.example.sanghyunj.speckerapp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanghyunj on 08/06/2017.
 */

public class ChatListConverter {

    private static Map<Integer, String> chatListConverter = new HashMap();


    static {
        chatListConverter.put(GlobalVariable.FRIEND_LIST_PROFILE_ME, GlobalVariable.FRIEND_LIST_TITLE_PROFILE_ME);
        chatListConverter.put(GlobalVariable.FRIEND_LIST_PROFILE_BOOKMARK, GlobalVariable.FRIEND_LIST_TITLE_PROFILE_BOOKMARK);
        chatListConverter.put(GlobalVariable.FRIEND_LIST_PROFILE_RECOMMEND_TEAM, GlobalVariable.FRIEND_LIST_TITLE_PROFILE_RECOMMEND_TEAM);
        chatListConverter.put(GlobalVariable.FRIEND_LIST_PROFILE_RECOMMEND_FRIEND, GlobalVariable.FRIEND_LIST_TITLE_PROFILE_RECOMMEND_FRIEND);
        chatListConverter.put(GlobalVariable.FRIEND_LIST_PROFILE_TEAM, GlobalVariable.FRIEND_LIST_TITLE_PROFILE_TEAM);
        chatListConverter.put(GlobalVariable.FRIEND_LIST_PROFILE_FRIEND, GlobalVariable.FRIEND_LIST_TITLE_PROFILE_FRIEND);
    }

    public static String code2String(int key){
        return chatListConverter.get(key);
    }

}
