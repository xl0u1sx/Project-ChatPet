package com.example.chatpet;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "ChatPet.db";
    private static final int DATABASE_VERSION = 1; // increment when updating .db file in assets


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}

// https://github.com/jgilfelt/android-sqlite-asset-helper
