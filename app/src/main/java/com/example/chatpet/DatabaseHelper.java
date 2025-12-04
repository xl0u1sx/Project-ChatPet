package com.example.chatpet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "ChatPet.db";
    private static final int DATABASE_VERSION = 6; // Incremented to add email column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // https://github.com/jgilfelt/android-sqlite-asset-helper
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Ensure all required columns exist when database is opened
        // This handles both fresh installs and upgrades
        ensureDatabaseSchema(db);
    }
    
    private void ensureDatabaseSchema(SQLiteDatabase db) {
        try {
            // Check and add email column if it doesn't exist
            android.database.Cursor emailCursor = db.rawQuery(
                    "PRAGMA table_info(users)", null
            );
            
            boolean hasEmail = false;
            if (emailCursor != null && emailCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String columnName = emailCursor.getString(emailCursor.getColumnIndex("name"));
                    if (columnName.equals("email")) {
                        hasEmail = true;
                        break;
                    }
                } while (emailCursor.moveToNext());
            }
            emailCursor.close();
            
            if (!hasEmail) {
                db.execSQL("ALTER TABLE users ADD COLUMN email TEXT");
                Log.d(TAG, "Added email column to users table");
            }
            
            // Check and add avatar_image column if it doesn't exist
            android.database.Cursor avatarCursor = db.rawQuery(
                    "PRAGMA table_info(users)", null
            );
            
            boolean hasAvatar = false;
            if (avatarCursor != null && avatarCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String columnName = avatarCursor.getString(avatarCursor.getColumnIndex("name"));
                    if (columnName.equals("avatar_image")) {
                        hasAvatar = true;
                        break;
                    }
                } while (avatarCursor.moveToNext());
            }
            avatarCursor.close();
            
            if (!hasAvatar) {
                db.execSQL("ALTER TABLE users ADD COLUMN avatar_image BLOB");
                Log.d(TAG, "Added avatar_image column to users table");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error ensuring database schema: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "========================================");
        Log.d(TAG, "UPGRADING DATABASE");
        Log.d(TAG, "Old version: " + oldVersion);
        Log.d(TAG, "New version: " + newVersion);
        Log.d(TAG, "========================================");

        if (oldVersion < 3) {
            try {
                android.database.Cursor cursor = db.rawQuery(
                        "PRAGMA table_info(chat_service)", null
                );

                boolean hasTimestamp = false;
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                        if (columnName.equals("timestamp")) {
                            hasTimestamp = true;
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (!hasTimestamp) {
                    db.execSQL("ALTER TABLE chat_service ADD COLUMN timestamp TEXT");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error adding timestamp column: " + e.getMessage(), e);
            }
        }

        if (oldVersion < 5) {
            try {
                android.database.Cursor cursor = db.rawQuery(
                        "PRAGMA table_info(users)", null
                );

                boolean hasAvatar = false;
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                        if (columnName.equals("avatar_image")) {
                            hasAvatar = true;
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (!hasAvatar) {
                    db.execSQL("ALTER TABLE users ADD COLUMN avatar_image BLOB");
                    Log.d(TAG, "Added avatar_image column to users table");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error adding avatar_image column: " + e.getMessage(), e);
            }
        }

        if (oldVersion < 6) {
            try {
                android.database.Cursor cursor = db.rawQuery(
                        "PRAGMA table_info(users)", null
                );

                boolean hasEmail = false;
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                        if (columnName.equals("email")) {
                            hasEmail = true;
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                if (!hasEmail) {
                    db.execSQL("ALTER TABLE users ADD COLUMN email TEXT");
                    Log.d(TAG, "Added email column to users table");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error adding email column: " + e.getMessage(), e);
            }
        }
    }
}