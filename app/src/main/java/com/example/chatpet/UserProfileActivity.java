package com.example.chatpet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private static final String PREFS_NAME = "PetActivityPrefs";
    private static final long TUCK_IN_COOLDOWN = 2 * 60 * 1000; // 2 minutes
    private static final int XP_FOR_LEVEL_UP = 100;

    // Intent keys
    public static final String EXTRA_USERNAME = "username";

    // Views
    private ImageView userAvatar;
    private TextView usernameText;
    private ImageView petImage;
    private TextView petNameText;
    private TextView petTypeText;
    private TextView petLevelText;
    private TextView petStatusText;
    private TextView xpText;
    private ProgressBar xpProgressBar;
    private TextView xpNextLevelText;
    private Button backButton;

    private String currentUsername;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        userAvatar = findViewById(R.id.userAvatar);
        usernameText = findViewById(R.id.usernameText);
        petImage = findViewById(R.id.petImage);
        petNameText = findViewById(R.id.petNameText);
        petTypeText = findViewById(R.id.petTypeText);
        petLevelText = findViewById(R.id.petLevelText);
        petStatusText = findViewById(R.id.petStatusText);
        xpText = findViewById(R.id.xpText);
        xpProgressBar = findViewById(R.id.xpProgressBar);
        xpNextLevelText = findViewById(R.id.xpNextLevelText);
        backButton = findViewById(R.id.backButton);

        // Initialize repository
        userRepository = new UserRepository(this);

        // Get username from intent
        currentUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "user123"; // Default fallback
        }

        // Load user profile data
        loadUserProfile();

        // Set up back button
        backButton.setOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        // Load username
        usernameText.setText(currentUsername);

        // Load user avatar
        byte[] avatarData = userRepository.getUserAvatar(currentUsername);
        if (avatarData != null && avatarData.length > 0) {
            Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarData, 0, avatarData.length);
            if (avatarBitmap != null) {
                userAvatar.setImageBitmap(avatarBitmap);
                Log.d(TAG, "Loaded user avatar for: " + currentUsername);
            }
        } else {
            // Keep default camera icon if no avatar
            Log.d(TAG, "No avatar found for user: " + currentUsername);
        }

        // Load pet info
        UserRepository.PetInfo petInfo = userRepository.getPetInfo(currentUsername);
        if (petInfo != null) {
            petNameText.setText(petInfo.getPetName());
            petTypeText.setText(petInfo.getPetType());
            
            // Get pet level from SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int petLevel = prefs.getInt(currentUsername + "_level", petInfo.getPetLevel());
            petLevelText.setText("Level " + petLevel);

            // Check if pet is sleeping
            boolean isSleeping = isPetSleeping(prefs);
            if (isSleeping) {
                petStatusText.setText("😴 Sleeping...");
            } else {
                petStatusText.setText("😊 Awake");
            }

            // Load correct pet image based on type, level, and sleeping status
            loadPetImage(petInfo.getPetType(), petLevel, isSleeping);

            // Load XP data
            int currentXP = prefs.getInt(currentUsername + "_xp", 0);
            xpProgressBar.setMax(XP_FOR_LEVEL_UP);
            xpProgressBar.setProgress(currentXP);
            xpText.setText(currentXP + " / " + XP_FOR_LEVEL_UP + " XP");

            // Calculate remaining XP needed
            int xpNeeded = XP_FOR_LEVEL_UP - currentXP;
            if (currentXP >= XP_FOR_LEVEL_UP && petLevel < 3) {
                xpNextLevelText.setText("Ready to level up!");
            } else if (petLevel >= 3) {
                xpNextLevelText.setText("Max level reached!");
            } else {
                xpNextLevelText.setText(xpNeeded + " XP needed for next level");
            }
        } else {
            petNameText.setText("No pet");
            petTypeText.setText("");
            petLevelText.setText("");
            petStatusText.setText("");
            Log.d(TAG, "No pet info found for user: " + currentUsername);
        }
    }

    private boolean isPetSleeping(SharedPreferences prefs) {
        long lastTuckInTime = prefs.getLong(currentUsername + "_lastTuckInTime", 0);
        if (lastTuckInTime == 0) {
            return false;
        }

        long elapsed = System.currentTimeMillis() - lastTuckInTime;
        return elapsed < TUCK_IN_COOLDOWN;
    }

    private void loadPetImage(String petType, int level, boolean isSleeping) {
        int imageResource;

        // If pet is sleeping, show the tuckIn image
        if (isSleeping) {
            String resourceName = petType.toLowerCase() + "lv" + level + "_tuckin";
            imageResource = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            
            // Fallback if tuckIn image not found
            if (imageResource == 0) {
                imageResource = getDefaultPetImage(petType, level);
            }
        } else {
            // Show normal pet image based on level
            imageResource = getDefaultPetImage(petType, level);
        }

        if (imageResource != 0) {
            petImage.setImageResource(imageResource);
            Log.d(TAG, "Loaded pet image for: " + petType + " level " + level + (isSleeping ? " (sleeping)" : " (awake)"));
        } else {
            Log.e(TAG, "Could not find pet image resource for: " + petType + " level " + level);
        }
    }

    private int getDefaultPetImage(String petType, int level) {
        if ("Dragon".equalsIgnoreCase(petType)) {
            switch (level) {
                case 1:
                    return R.drawable.dragon_level1_transparent;
                case 2:
                    return R.drawable.dragon_level2_transparent;
                case 3:
                    return R.drawable.dragon_level3_transparent;
                default:
                    return R.drawable.dragon_level1_transparent;
            }
        } else { // Unicorn
            switch (level) {
                case 1:
                    return R.drawable.unicorn_level1_transparent;
                case 2:
                    return R.drawable.unicorn_level2_transparent;
                case 3:
                    return R.drawable.unicorn_level3_transparent;
                default:
                    return R.drawable.unicorn_level1_transparent;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile data when returning to this activity
        loadUserProfile();
    }
}

