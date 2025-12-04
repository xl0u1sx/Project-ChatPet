package com.example.chatpet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context); // use dbhelper 
        // https://github.com/jgilfelt/android-sqlite-asset-helper
    }

    /**
     * check username and password
     * return User obj if it is in db, null otherwise
     * 
     * database driver helper: 
     * // https://github.com/jgilfelt/android-sqlite-asset-helper
     */
    public User authenticate(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"username", "password", "first_name", "last_name"};
            String selection = "username = ? AND password = ?";
            String[] selectionArgs = {username, password};

            cursor = db.query(
                "users",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String foundUsername = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String foundPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));

                Log.d(TAG, "User authenticated successfully: " + foundUsername);
                return new User(foundUsername, foundPassword, firstName, lastName);
            } else {
                Log.d(TAG, "Authentication failed for username: " + username);
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during authentication", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Check if a username already exists
     */
    public boolean userExists(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"username"};
            String selection = "username = ?";
            String[] selectionArgs = {username};

            cursor = db.query(
                "users",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            );

            boolean exists = cursor != null && cursor.moveToFirst();
            Log.d(TAG, "User exists check for " + username + ": " + exists);
            return exists;

        } catch (Exception e) {
            Log.e(TAG, "Error checking if user exists", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Register a new user
     */
    public boolean registerUser(User user) {
        SQLiteDatabase db = null;

        try {
            // Check if user already exists
            if (userExists(user.getUsername())) {
                Log.d(TAG, "Username already exists: " + user.getUsername());
                return false;
            }

            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("password", user.getPassword());
            values.put("first_name", user.getFirstName());
            values.put("last_name", user.getLastName());
            
            // Add email if present
            if (user.getEmail() != null) {
                values.put("email", user.getEmail());
                Log.d(TAG, "Adding email: " + user.getEmail());
            }
            
            // Add avatar image if present
            if (user.getAvatarImage() != null) {
                values.put("avatar_image", user.getAvatarImage());
                Log.d(TAG, "Adding avatar image, size: " + user.getAvatarImage().length + " bytes");
            }

            Log.d(TAG, "Attempting to insert user: " + user.getUsername());
            long result = db.insert("users", null, values);

            if (result != -1) {
                Log.d(TAG, "User registered successfully: " + user.getUsername() + " (ID: " + result + ")");
                return true;
            } else {
                Log.e(TAG, "Failed to register user: " + user.getUsername() + " - Insert returned -1");
                return false;
            }
            

        } catch (Exception e) {
            Log.e(TAG, "Error during user registration for user: " + user.getUsername(), e);
            Log.e(TAG, "Exception message: " + e.getMessage());
            Log.e(TAG, "Exception cause: " + e.getCause());
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Creates a pet for a user
     * create pet for user, 
     * store username, petName, and petType
     */
    public boolean createPetForUser(String username, String petName, String petType) {
        // return true if success
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("pet_name", petName);
            values.put("pet_type", petType);
            values.put("username", username);
            values.put("happiness_meter", 50);
            values.put("energy_meter", 100);
            values.put("hunger_meter", 100);
            values.put("pet_level", 1);
            values.put("level_progress", 0);
            values.put("times_fed", 0);
            values.put("times_tucked_in", 0);

            Log.d(TAG, "Creating pet - Name: " + petName + ", Type: " + petType + ", Username: " + username);
            
            long result = db.insert("pet_services", null, values);

            if (result != -1) {
                Log.d(TAG, "Pet created successfully for user: " + username + " (ID: " + result + ")");
                return true;
            } else {
                Log.e(TAG, "Failed to create pet for user: " + username);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating pet for user", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * check if user already has pet
     */
    public boolean userHasPet(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"pet_id"};
            String selection = "username = ?";
            String[] selectionArgs = {username};

            cursor = db.query(
                "pet_services",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            );

            boolean hasPet = cursor != null && cursor.moveToFirst();
            Log.d(TAG, "User has pet check for " + username + ": " + hasPet);
            return hasPet;

        } catch (Exception e) {
            Log.e(TAG, "Error checking if user has pet", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Get pet information for a user
     * Returns a PetInfo object containing pet name and type, or null if not found
     */
    public PetInfo getPetInfo(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"pet_name", "pet_type", "pet_level"};
            String selection = "username = ?";
            String[] selectionArgs = {username};

            cursor = db.query(
                "pet_services",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String petName = cursor.getString(cursor.getColumnIndexOrThrow("pet_name"));
                String petType = cursor.getString(cursor.getColumnIndexOrThrow("pet_type"));
                int petLevel = cursor.getInt(cursor.getColumnIndexOrThrow("pet_level"));
                
                Log.d(TAG, "Pet info retrieved for " + username + ": " + petName + " (" + petType + ") Level " + petLevel);
                return new PetInfo(petName, petType, petLevel);
            } else {
                Log.d(TAG, "No pet found for user: " + username);
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting pet info", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Save a journal entry to the database
     */
    public boolean saveJournalEntry(String username, JournalEntry entry) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("journal_entry", entry.getJournalText());
            values.put("date", entry.getDate());
            values.put("time", entry.getTime());
            values.put("username", username);

            long result = db.insert("journal_service", null, values);

            if (result != -1) {
                Log.d(TAG, "Journal entry saved successfully for user: " + username);
                return true;
            } else {
                Log.e(TAG, "Failed to save journal entry for user: " + username);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving journal entry", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Get all journal entries for a user, ordered by newest first
     */
    public List<JournalEntry> getJournalEntries(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<JournalEntry> entries = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"entry_id", "journal_entry", "date", "time"};
            String selection = "username = ?";
            String[] selectionArgs = {username};
            String orderBy = "entry_id DESC"; // Newest first

            cursor = db.query(
                "journal_service",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int entryId = cursor.getInt(cursor.getColumnIndexOrThrow("entry_id"));
                    String journalText = cursor.getString(cursor.getColumnIndexOrThrow("journal_entry"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));

                    JournalEntry entry = new JournalEntry(entryId, journalText, date, time, username);
                    entries.add(entry);
                } while (cursor.moveToNext());
                
                Log.d(TAG, "Retrieved " + entries.size() + " journal entries for user: " + username);
            } else {
                Log.d(TAG, "No journal entries found for user: " + username);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving journal entries", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return entries;
    }

    /**
     * Get the most recent journal entry for a user
     */
    public JournalEntry getLatestJournalEntry(String username) {
        List<JournalEntry> entries = getJournalEntries(username);
        if (!entries.isEmpty()) {
            return entries.get(0); // Already ordered newest first
        }
        return null;
    }

    /**
     * Update pet level in database
     */
    public boolean updatePetLevel(String username, int newLevel) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put("pet_level", newLevel);
            
            int rowsAffected = db.update(
                "pet_services",
                values,
                "username = ?",
                new String[]{username}
            );
            
            if (rowsAffected > 0) {
                Log.d(TAG, "Updated pet level to " + newLevel + " for user: " + username);
                return true;
            } else {
                Log.e(TAG, "Failed to update pet level for user: " + username);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating pet level", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Simple class to hold pet information
     */
    public static class PetInfo {
        private final String petName;
        private final String petType;
        private final int petLevel;

        public PetInfo(String petName, String petType, int petLevel) {
            this.petName = petName;
            this.petType = petType;
            this.petLevel = petLevel;
        }

        public String getPetName() {
            return petName;
        }

        public String getPetType() {
            return petType;
        }
        
        public int getPetLevel() {
            return petLevel;
        }
    }

    /**
     * Save a chat message to the existing chat_service table
     */
    public boolean saveChatMessage(String username, String role, String message, String timestamp, String chatDate) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("message", message);
            values.put("chat_date", chatDate);
            values.put("role", role);  // NEW column
            values.put("timestamp", timestamp);  // NEW column

            long result = db.insert("chat_service", null, values);

            if (result != -1) {
                Log.d(TAG, "Chat message saved for user: " + username);
                return true;
            } else {
                Log.e(TAG, "Failed to save chat message for user: " + username);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving chat message", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    /**
     * Get chat messages for a user on a specific date from chat_service table
     */
    public List<ChatMessageData> getChatMessages(String username, String chatDate) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<ChatMessageData> messages = new ArrayList<>();

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"message_id", "message", "role", "timestamp"};
            String selection = "username = ? AND chat_date = ?";
            String[] selectionArgs = {username, chatDate};
            String orderBy = "message_id ASC"; // Oldest first to maintain conversation order

            cursor = db.query(
                    "chat_service",
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int messageId = cursor.getInt(cursor.getColumnIndexOrThrow("message_id"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));

                    // Handle nullable columns for backward compatibility
                    int roleIndex = cursor.getColumnIndex("role");
                    String role = (roleIndex != -1) ? cursor.getString(roleIndex) : "user";

                    int timestampIndex = cursor.getColumnIndex("timestamp");
                    String timestamp = (timestampIndex != -1) ? cursor.getString(timestampIndex) : "";

                    ChatMessageData msgData = new ChatMessageData(messageId, role, message, timestamp);
                    messages.add(msgData);
                } while (cursor.moveToNext());

                Log.d(TAG, "Retrieved " + messages.size() + " chat messages for user: " + username + " on date: " + chatDate);
            } else {
                Log.d(TAG, "No chat messages found for user: " + username + " on date: " + chatDate);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving chat messages", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return messages;
    }

    /**
     * Clear chat messages for a specific date (optional - for cleanup)
     */
    public boolean clearChatMessagesForDate(String username, String chatDate) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            int rowsDeleted = db.delete(
                    "chat_service",
                    "username = ? AND chat_date = ?",
                    new String[]{username, chatDate}
            );

            if (rowsDeleted > 0) {
                Log.d(TAG, "Cleared " + rowsDeleted + " chat messages for user: " + username + " on date: " + chatDate);
                return true;
            } else {
                Log.d(TAG, "No chat messages to clear for user: " + username + " on date: " + chatDate);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error clearing chat messages", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Simple class to hold chat message data from database
     */
    public static class ChatMessageData {
        private final int messageId;
        private final String role;
        private final String message;
        private final String timestamp;

        public ChatMessageData(int messageId, String role, String message, String timestamp) {
            this.messageId = messageId;
            this.role = role;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getMessageId() {
            return messageId;
        }

        public String getRole() {
            return role;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Get user's avatar image
     */
    public byte[] getUserAvatar(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"avatar_image"};
            String selection = "username = ?";
            String[] selectionArgs = {username};

            cursor = db.query(
                "users",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int avatarIndex = cursor.getColumnIndex("avatar_image");
                if (avatarIndex != -1 && !cursor.isNull(avatarIndex)) {
                    return cursor.getBlob(avatarIndex);
                }
            }

            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user avatar", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Update user avatar image
     */
    public boolean updateUserAvatar(String username, byte[] avatarImage) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("avatar_image", avatarImage);

            String whereClause = "username = ?";
            String[] whereArgs = {username};

            int rowsAffected = db.update("users", values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Avatar updated successfully for user: " + username);
                return true;
            } else {
                Log.w(TAG, "No user found to update avatar for: " + username);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating user avatar", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
