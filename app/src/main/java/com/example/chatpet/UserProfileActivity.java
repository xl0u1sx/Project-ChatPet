package com.example.chatpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private static final String PREFS_NAME = "PetActivityPrefs";
    private static final long TUCK_IN_COOLDOWN = 30 * 1000; // 30 seconds
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
    private TextView maxLevelReachedText;
    private Button backButton;
    private Button changeAvatarButton;

    private String currentUsername;
    private UserRepository userRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
        maxLevelReachedText = findViewById(R.id.maxLevelReachedText);
        backButton = findViewById(R.id.backButton);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);

        // Initialize repository
        userRepository = new UserRepository(this);

        // Get username from intent
        currentUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "user123"; // Default fallback
        }

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleImageSelection(imageUri);
                    }
                }
            }
        );

        // Load user profile data
        loadUserProfile();

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Set up change avatar button
        changeAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
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
            
            // Check if pet is at max level
            if (petLevel >= 3) {
                // Max level reached - hide progress bar and XP text, show max level message
                xpProgressBar.setVisibility(View.GONE);
                xpText.setVisibility(View.GONE);
                xpNextLevelText.setVisibility(View.GONE);
                maxLevelReachedText.setVisibility(View.VISIBLE);
            } else {
                // Still leveling - show progress bar and XP text, hide max level message
                xpProgressBar.setVisibility(View.VISIBLE);
                xpText.setVisibility(View.VISIBLE);
                xpNextLevelText.setVisibility(View.VISIBLE);
                maxLevelReachedText.setVisibility(View.GONE);
                
                xpProgressBar.setMax(XP_FOR_LEVEL_UP);
                xpProgressBar.setProgress(currentXP);
                xpText.setText(currentXP + " / " + XP_FOR_LEVEL_UP + " XP");

                // Calculate remaining XP needed
                int xpNeeded = XP_FOR_LEVEL_UP - currentXP;
                if (currentXP >= XP_FOR_LEVEL_UP) {
                    xpNextLevelText.setText("Ready to level up!");
                } else {
                    xpNextLevelText.setText(xpNeeded + " XP needed for next level");
                }
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

    /**
     * Handle image selection from gallery
     */
    private void handleImageSelection(Uri imageUri) {
        try {
            // Load the image as a bitmap
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            if (bitmap != null) {
                // Resize the bitmap to a reasonable size (e.g., 300x300)
                Bitmap resizedBitmap = resizeBitmap(bitmap, 300, 300);
                
                // Display the image in the avatar view
                userAvatar.setImageBitmap(resizedBitmap);
                
                // Convert to byte array for database storage
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] avatarImageData = stream.toByteArray();
                
                // Update avatar in database
                boolean success = userRepository.updateUserAvatar(currentUsername, avatarImageData);
                
                if (success) {
                    Log.d(TAG, "Avatar updated successfully, size: " + avatarImageData.length + " bytes");
                    Toast.makeText(this, "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update avatar in database");
                    Toast.makeText(this, "Failed to update avatar", Toast.LENGTH_SHORT).show();
                }
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling image selection", e);
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Resize bitmap to fit within max dimensions while maintaining aspect ratio
     */
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);
        
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}

