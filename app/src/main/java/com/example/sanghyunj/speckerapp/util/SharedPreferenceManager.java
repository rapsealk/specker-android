package com.example.sanghyunj.speckerapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rapsealk on 2017. 11. 17..
 */

public class SharedPreferenceManager {

    private static SharedPreferenceManager mInstance = null;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEdit;

    private static final String FILE_NAME = "Specker_pref";
    private static final String CHATROOM_ON_USE = "CHATROOM_";

    public static SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) mInstance = new SharedPreferenceManager(context);
        return mInstance;
    }

    private SharedPreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mEdit = mSharedPreferences.edit();
    }

    public boolean getRoomStatus(String roomId) {
        return mSharedPreferences.getBoolean(CHATROOM_ON_USE + roomId, false);
    }

    public void setRoomStatus(String roomId, boolean onUse) {
        mEdit.putBoolean(CHATROOM_ON_USE + roomId, onUse);
    }
}