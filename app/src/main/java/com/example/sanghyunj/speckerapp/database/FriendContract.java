package com.example.sanghyunj.speckerapp.database;

import android.provider.BaseColumns;

/**
 * Created by rapsealk on 2017. 11. 17..
 */

public final class FriendContract {

    private FriendContract() {}

    public static class FriendEntry implements BaseColumns {
        public static final String TABLE_NAME = "friend";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_FRIEND_ID = "friend_id";
        public static final String COLUMN_NAME_PROFILE_IMAGE = "profile_image";
        public static final String COLUMN_NAME_STATUS_MESSAGE = "status_message";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FriendEntry.TABLE_NAME +
            " (" + FriendEntry._ID + " INTEGER PRIMARY KEY, " +
            FriendEntry.COLUMN_NAME_USER_ID + " TEXT, " +
            FriendEntry.COLUMN_NAME_FRIEND_ID + " TEXT, " +
            FriendEntry.COLUMN_NAME_PROFILE_IMAGE + " TEXT, " +
            FriendEntry.COLUMN_NAME_STATUS_MESSAGE + " TEXT, " +
            FriendEntry.COLUMN_NAME_TIMESTAMP + " INTEGER" + " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FriendEntry.TABLE_NAME;
}
