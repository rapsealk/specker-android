package com.example.sanghyunj.speckerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sanghyunj.speckerapp.retrofit.Response.Friend;

import java.util.ArrayList;

import static com.example.sanghyunj.speckerapp.database.FriendContract.*;
import static com.example.sanghyunj.speckerapp.database.FriendContract.FriendEntry.*;

/**
 * Created by GL552 on 2017-11-24.
 */

public class FriendDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SpeckerFriend.db";

    public FriendDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public long insertFriend(String userId, Friend friend) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(COLUMN_NAME_USER_ID, userId);
        value.put(COLUMN_NAME_FRIEND_ID, friend.getId());
        value.put(COLUMN_FRIEND_NAME, friend.getName());
        value.put(COLUMN_NAME_PROFILE_IMAGE, friend.getProfile());
        value.put(COLUMN_NAME_STATUS_MESSAGE, "");  // TODO
        value.put(COLUMN_NAME_TIMESTAMP, friend.getTimestamp());
        long newRowId = db.insert(TABLE_NAME, null, value);
        return newRowId;
    }

    // TODO updateFriend, deleteFriend

    public ArrayList<Friend> getFriends() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                _ID,
                COLUMN_NAME_FRIEND_ID,
                COLUMN_FRIEND_NAME,
                COLUMN_NAME_PROFILE_IMAGE,
                COLUMN_NAME_TIMESTAMP
        };
        String sortOrder = COLUMN_FRIEND_NAME + " DESC"; // COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, sortOrder);
        cursor.moveToFirst();

        ArrayList<Friend> mFriends = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            String _id = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FRIEND_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_NAME));
            String gravatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROFILE_IMAGE));
            long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIMESTAMP));
            Friend mFriend = new Friend(_id, name, gravatar, timestamp);
            mFriends.add(mFriend);
        }
        return mFriends;
    }

    public Friend getFriendById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                _ID,
                COLUMN_NAME_FRIEND_ID,
                COLUMN_FRIEND_NAME,
                COLUMN_NAME_PROFILE_IMAGE,
                COLUMN_NAME_TIMESTAMP
        };
        String selection = COLUMN_NAME_FRIEND_ID + " = ?";
        String[] selectionArgs = { id };

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) return null;
        Friend friend = new Friend(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FRIEND_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PROFILE_IMAGE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIMESTAMP))
        );
        return friend;
    }
}
