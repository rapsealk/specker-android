package com.example.sanghyunj.speckerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sanghyunj.speckerapp.model.ChatMessage;
import com.example.sanghyunj.speckerapp.retrofit.Response.Friend;

import static com.example.sanghyunj.speckerapp.database.ChatContract.*;
import static com.example.sanghyunj.speckerapp.database.ChatContract.ChatEntry.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rapsealk on 2017. 11. 8..
 */

public class ChatDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SpeckerChat.db";

    private FriendDbHelper mFriendDbHelper;
    private HashMap<String, String> profileMap;

    // TODO Singleton
    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mFriendDbHelper = new FriendDbHelper(context);
        profileMap = new HashMap<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertChat(String room, String author, String message, long timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ROOM, room);
        values.put(COLUMN_NAME_AUTHOR, author);
        values.put(COLUMN_NAME_MESSAGE, message);
        values.put(COLUMN_NAME_TIMESTAMP, timestamp);
        long newRowId = db.insert(TABLE_NAME, null, values);
        return newRowId;
    }

    public int removeChat(String room) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_NAME_ROOM + " = ?";
        String[] selectionArgs = { room };
        int result = db.delete(TABLE_NAME, selection, selectionArgs);
        return result;
    }

    public long getLastChatTimestamp(String roomId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                _ID,
                COLUMN_NAME_ROOM,
                COLUMN_NAME_AUTHOR,
                COLUMN_NAME_MESSAGE,
                COLUMN_NAME_TIMESTAMP
        };
        String selection = COLUMN_NAME_ROOM + " = ?";
        String[] selectionArgs = { roomId };
        String sortOrder = COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) return -1;
        try {
            return cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIMESTAMP));
        } catch (IllegalArgumentException exception) {
            return -1;
        }
    }

    public ArrayList<ChatMessage> getChatHistory(String roomId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_NAME_AUTHOR,
                COLUMN_NAME_MESSAGE,
                COLUMN_NAME_TIMESTAMP
        };
        String selection = COLUMN_NAME_ROOM + " = ?";
        String[] selectionArgs = { roomId };
        String sortOrder = COLUMN_NAME_TIMESTAMP;

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();

        ArrayList<ChatMessage> mChatMessages = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            String author = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AUTHOR));
            String profile = profileMap.get(author);
            if (profile == null) {
                Friend friend = mFriendDbHelper.getFriendById(author);
                if (friend != null) {
                    profile = friend.getProfile();
                    profileMap.put(author, profile);
                }
            }
            String message = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MESSAGE));
            long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIMESTAMP));
            mChatMessages.add(new ChatMessage(message, author, profile, ""));
            cursor.moveToNext();
        }
        // TODO PROFILE
        return mChatMessages;
    }

    public boolean ensureTimestamp(String roomId, long timestamp) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_NAME_AUTHOR,
                COLUMN_NAME_MESSAGE,
                COLUMN_NAME_TIMESTAMP
        };
        String selection = COLUMN_NAME_ROOM + " = ? AND " + COLUMN_NAME_TIMESTAMP + " = ?";
        String[] selectionArgs = { roomId, Long.toString(timestamp) };

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        return (cursor.getCount() == 0);
    }
}