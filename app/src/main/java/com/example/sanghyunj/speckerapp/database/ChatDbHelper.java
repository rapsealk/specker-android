package com.example.sanghyunj.speckerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sanghyunj.speckerapp.model.ChatMessage;

import java.util.ArrayList;

/**
 * Created by rapsealk on 2017. 11. 8..
 */

public class ChatDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SpeckerChat.db";

    // TODO Singleton
    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChatContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ChatContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertChat(String room, String author, String message, long timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatContract.ChatEntry.COLUMN_NAME_ROOM, room);
        values.put(ChatContract.ChatEntry.COLUMN_NAME_AUTHOR, author);
        values.put(ChatContract.ChatEntry.COLUMN_NAME_MESSAGE, message);
        values.put(ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP, timestamp);
        long newRowId = db.insert(ChatContract.ChatEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    public long getLastChatTimestamp(String roomId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ChatContract.ChatEntry._ID,
                ChatContract.ChatEntry.COLUMN_NAME_ROOM,
                ChatContract.ChatEntry.COLUMN_NAME_AUTHOR,
                ChatContract.ChatEntry.COLUMN_NAME_MESSAGE,
                ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP
        };
        String selection = ChatContract.ChatEntry.COLUMN_NAME_ROOM + " = ?";
        String[] selectionArgs = { roomId };
        String sortOrder = ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(ChatContract.ChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) return -1;
        try {
            return cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
        } catch (IllegalArgumentException exception) {
            return -1;
        }
    }

    public ArrayList<ChatMessage> getChatHistory(String roomId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ChatContract.ChatEntry.COLUMN_NAME_AUTHOR,
                ChatContract.ChatEntry.COLUMN_NAME_MESSAGE,
                ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP
        };
        String selection = ChatContract.ChatEntry.COLUMN_NAME_ROOM + " = ?";
        String[] selectionArgs = { roomId };
        String sortOrder = ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP;

        Cursor cursor = db.query(ChatContract.ChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();

        ArrayList<ChatMessage> mChatMessages = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String message = cursor.getString(cursor.getColumnIndex("message"));
            long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
            mChatMessages.add(new ChatMessage(message, author, "", ""));
            cursor.moveToNext();
        }
        return mChatMessages;
    }

    public boolean ensureTimestamp(String roomId, long timestamp) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ChatContract.ChatEntry.COLUMN_NAME_AUTHOR,
                ChatContract.ChatEntry.COLUMN_NAME_MESSAGE,
                ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP
        };
        String selection = ChatContract.ChatEntry.COLUMN_NAME_ROOM + " = ? AND " + ChatContract.ChatEntry.COLUMN_NAME_TIMESTAMP + " = ?";
        String[] selectionArgs = { roomId, Long.toString(timestamp) };

        Cursor cursor = db.query(ChatContract.ChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        return (cursor.getCount() == 0);
    }
}