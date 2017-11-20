package com.example.sanghyunj.speckerapp.database;

import android.provider.BaseColumns;

/**
 * Created by rapsealk on 2017. 11. 11..
 */

public final class ChatContract {

    private ChatContract() {}

    public static class ChatEntry implements BaseColumns {
        public static final String TABLE_NAME = "chat";
        public static final String COLUMN_NAME_ROOM = "room";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ChatEntry.TABLE_NAME +
            " (" + ChatEntry._ID + " INTEGER PRIMARY KEY, " +
            ChatEntry.COLUMN_NAME_ROOM + " TEXT, " +
            ChatEntry.COLUMN_NAME_AUTHOR + " TEXT, " +
            ChatEntry.COLUMN_NAME_MESSAGE + " TEXT, " +
            ChatEntry.COLUMN_NAME_TIMESTAMP + " INTEGER" + " )";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ChatEntry.TABLE_NAME;
}
