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

            long result = db.insert("users", null, values);

            if (result != -1) {
                Log.d(TAG, "User registered successfully: " + user.getUsername());
                return true;
            } else {
                Log.e(TAG, "Failed to register user: " + user.getUsername());
                return false;
            }
            

        } catch (Exception e) {
            Log.e(TAG, "Error during user registration", e);
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

            String[] columns = {"pet_name", "pet_type"};
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
                
                Log.d(TAG, "Pet info retrieved for " + username + ": " + petName + " (" + petType + ")");
                return new PetInfo(petName, petType);
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
     * Simple class to hold pet information
     */
    public static class PetInfo {
        private final String petName;
        private final String petType;

        public PetInfo(String petName, String petType) {
            this.petName = petName;
            this.petType = petType;
        }

        public String getPetName() {
            return petName;
        }

        public String getPetType() {
            return petType;
        }
    }
}
