package com.example.chatpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PetActivity extends AppCompatActivity {
    
    private static final String TAG = "PetActivity";
    
    private final PetService petService = new PetService();
    @Nullable private Pet pet;

    // Views (IDs must match activity_pet.xml)
    private TextView petTitle;
    private TextView petType;
    private TextView petName;
    private TextView petLevel;
    private ProgressBar happinessProg;
    private ProgressBar energyProg;
    private ProgressBar hungerProg;
    private Button feedButton;
    private Button tuckInButton;
    private Button upgradePetButton;
    private Button breatheFireButton;
    private Button tellStoryButton;
    private ImageView petImage;
    private TextView statusText;

    // Intent keys (as you defined)
    public static final String temp_user_id = "temp_user_id";
    public static final String temp_pet_name = "temp_pet_name";
    public static final String temp_pet_type = "temp_pet_type";
    
    // SharedPreferences for tracking tuck-in cooldown and pet state
    private static final String PREFS_NAME = "PetActivityPrefs";
    private static final long TUCK_IN_COOLDOWN = 2 * 60 * 1000; // 2 minutes in milliseconds (for demo)
    
    // Keys for persisting pet state (will be prefixed with username)
    private static final String KEY_HAPPINESS = "_happiness";
    private static final String KEY_ENERGY = "_energy";
    private static final String KEY_HUNGER = "_hunger";
    private static final String KEY_LEVEL = "_level";
    private static final String KEY_LAST_SAVE = "_lastSaveTime";
    private static final String KEY_LAST_TUCK_IN = "_lastTuckInTime";
    
    private String currentUsername; // To track which user's pet we're managing
    
    // Handler for meter decay timer
    private Handler meterDecayHandler;
    private Runnable meterDecayRunnable;
    private static final long DECAY_INTERVAL = 60 * 1000; // 1 minute in milliseconds
    
    // Handler for tracking happiness > 80 for upgrade eligibility
    private Handler upgradeCheckHandler;
    private Runnable upgradeCheckRunnable;
    private long happinessAbove80StartTime = 0;
    private boolean isHappinessAbove80 = false;
    private static final long UPGRADE_REQUIREMENT_TIME = 60 * 1000; // 1 minute in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        petTitle = findViewById(R.id.petTitle);
        petType = findViewById(R.id.petType);
        petName = findViewById(R.id.petName);
        petLevel = findViewById(R.id.petLevel);
        happinessProg = findViewById(R.id.happinessProg);
        energyProg = findViewById(R.id.energyProg);
        hungerProg = findViewById(R.id.hungerProg);
        feedButton = findViewById(R.id.feedButton);
        tuckInButton = findViewById(R.id.tuckInButton);
        upgradePetButton = findViewById(R.id.upgradePetButton);
        breatheFireButton = findViewById(R.id.breatheFireButton);
        tellStoryButton = findViewById(R.id.tellStoryButton);
        petImage = findViewById(R.id.petImage);
        statusText = findViewById(R.id.statusText);

        // progress bars
        if (happinessProg != null) happinessProg.setMax(100);
        if (energyProg != null)    energyProg.setMax(100);
        if (hungerProg != null)    hungerProg.setMax(100);

        initFromIntent(getIntent());
        setupButtons();
        setupMeterDecay();
        setupUpgradeCheck();
        refreshMeters();
        updateLevelDisplay();
    }


    private void initFromIntent(@Nullable Intent intent) {
        // Read extras with safe defaults
        String type = "Unicorn";
        String name = "Pet";
        String uid = "temp_user_id";

        if(intent!=null){
            String intentType = intent.getStringExtra(temp_pet_type);
            String intentName = intent.getStringExtra(temp_pet_name);
            String intentUid = intent.getStringExtra(temp_user_id);
            
            if(intentType != null) type = intentType;
            if(intentName != null) name = intentName;
            if(intentUid != null) uid = intentUid;
        }
        
        currentUsername = uid;
        pet = petService.createPet(type, name, uid);
        
        // Load saved pet state from SharedPreferences
        loadPetState();

        if (petTitle != null) petTitle.setText("");
        if (pet != null) {
            if (petType != null) petType.setText(pet.getPetType());
            if (petName != null)     petName.setText(pet.getPetName());
        }

        // Update pet image based on level
        updatePetImage();


        if(pet!=null){
            //pet.getPetType().equals("Dragon")
            if(pet instanceof Dragon){
                if (breatheFireButton != null) breatheFireButton.setVisibility(View.VISIBLE);
                if (tellStoryButton != null)   tellStoryButton.setVisibility(View.GONE);
            }
            else{
                if (breatheFireButton != null) breatheFireButton.setVisibility(View.GONE);
                if (tellStoryButton != null)   tellStoryButton.setVisibility(View.VISIBLE);
            }

        }


        if (statusText != null) {
            statusText.setText(pet != null ? ("Say hi to " + pet.getPetName() + "!") : "No pet found.");
        }
    }

    private void refreshMeters() {
        if (pet == null) return;
        PetState.Meters m = pet.getPetState();
        if (happinessProg != null) happinessProg.setProgress(m.happiness);
        if (energyProg != null)    energyProg.setProgress(m.energy);
        if (hungerProg != null)    hungerProg.setProgress(m.hunger);
    }

    private void setupButtons() {
        // Feed button - increases hunger by 30, increase energy by 10
        if (feedButton != null) {
            feedButton.setOnClickListener(v -> {
                if (pet == null) return;
                String msg = pet.feed();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
                savePetState();
            });
        }

        // Tuck-in button - fills energy to 100, but only once per cooldown period
        if (tuckInButton != null) {
            tuckInButton.setOnClickListener(v -> {
                if (pet == null || currentUsername == null) return;
                
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                long lastTuckInTime = prefs.getLong(currentUsername + KEY_LAST_TUCK_IN, 0);
                long currentTime = System.currentTimeMillis();
                long timeSinceLastTuckIn = currentTime - lastTuckInTime;
                
                if (timeSinceLastTuckIn >= TUCK_IN_COOLDOWN) {
                    // Tuck-in is available
                    String msg = pet.tuckIn();
                    prefs.edit().putLong(currentUsername + KEY_LAST_TUCK_IN, currentTime).apply();
                    Log.d(TAG, "Tuck-in used for user: " + currentUsername);
                    if (statusText != null) statusText.setText(msg);
                    refreshMeters();
                    savePetState();
                } else {
                    // Still on cooldown
                    long remainingTime = TUCK_IN_COOLDOWN - timeSinceLastTuckIn;
                    long hoursRemaining = remainingTime / (60 * 60 * 1000);
                    long minutesRemaining = (remainingTime % (60 * 60 * 1000)) / (60 * 1000);
                    long secondsRemaining = (remainingTime % (60 * 1000)) / 1000;
                    
                    Log.d(TAG, "Tuck-in on cooldown for user: " + currentUsername + 
                          " - Remaining: " + minutesRemaining + "m " + secondsRemaining + "s");
                    
                    // Build user-friendly cooldown message
                    StringBuilder cooldownMsg = new StringBuilder(pet.getPetName() + " doesn't want to go to sleep yet! Please wait ");
                    if (hoursRemaining > 0) {
                        cooldownMsg.append(hoursRemaining).append(" hour").append(hoursRemaining > 1 ? "s" : "");
                        if (minutesRemaining > 0) {
                            cooldownMsg.append(" and ").append(minutesRemaining).append(" minute").append(minutesRemaining > 1 ? "s" : "");
                        }
                    } else if (minutesRemaining > 0) {
                        cooldownMsg.append(minutesRemaining).append(" minute").append(minutesRemaining > 1 ? "s" : "");
                        if (secondsRemaining > 0) {
                            cooldownMsg.append(" and ").append(secondsRemaining).append(" second").append(secondsRemaining > 1 ? "s" : "");
                        }
                    } else {
                        cooldownMsg.append(secondsRemaining).append(" second").append(secondsRemaining > 1 ? "s" : "");
                    }
                    cooldownMsg.append(".");
                    
                    if (statusText != null) statusText.setText(cooldownMsg.toString());
                }
            });
        }
        
        // Upgrade pet button - increases level by 1 (appears when happiness > 80 for 1 minute)
        if (upgradePetButton != null) {
            upgradePetButton.setOnClickListener(v -> {
                if (pet == null) return;
                
                PetState petStateObj = pet.getPetStateObject();
                if (petStateObj.canLevelUp()) {
                    petStateObj.levelUp();
                    updateLevelDisplay();
                    if (statusText != null) {
                        statusText.setText("🎉 " + pet.getPetName() + " leveled up to Level " + 
                                         pet.getPetLevel() + "! They've grown more mature!");
                    }
                    // Hide the button after upgrading
                    upgradePetButton.setVisibility(View.GONE);
                    // Reset happiness tracking
                    isHappinessAbove80 = false;
                    happinessAbove80StartTime = 0;
                    // Save the new level
                    savePetState();
                } else {
                    if (statusText != null) {
                        statusText.setText(pet.getPetName() + " has reached max level!");
                    }
                }
            });
        }

        // Special action buttons for each pet type
        if (breatheFireButton != null) {
            breatheFireButton.setOnClickListener(v -> {
                if (!(pet instanceof Dragon)) return;
                String msg = ((Dragon) pet).breatheFire();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
                savePetState();
            });
        }

        if (tellStoryButton != null) {
            tellStoryButton.setOnClickListener(v -> {
                if (!(pet instanceof Unicorn)) return;
                String msg = ((Unicorn) pet).tellMagicalStory();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
                savePetState();
            });
        }
    }

    private void setupMeterDecay() {
        meterDecayHandler = new Handler(Looper.getMainLooper());
        meterDecayRunnable = new Runnable() {
            @Override
            public void run() {
                applyMeterDecay();
                // Schedule next decay
                meterDecayHandler.postDelayed(this, DECAY_INTERVAL);
            }
        };
    }
    
    private void setupUpgradeCheck() {
        upgradeCheckHandler = new Handler(Looper.getMainLooper());
        upgradeCheckRunnable = new Runnable() {
            @Override
            public void run() {
                checkUpgradeEligibility();
                // Check every second for responsiveness
                upgradeCheckHandler.postDelayed(this, 1000);
            }
        };
    }
    
    private void checkUpgradeEligibility() {
        if (pet == null || upgradePetButton == null) return;
        
        PetState.Meters currentMeters = pet.getPetState();
        long currentTime = System.currentTimeMillis();
        
        // Check if happiness is above 80
        if (currentMeters.happiness > 80) {
            if (!isHappinessAbove80) {
                // Just crossed the threshold
                isHappinessAbove80 = true;
                happinessAbove80StartTime = currentTime;
            } else {
                // Check if it's been above 80 for 1 minute
                long timeAbove80 = currentTime - happinessAbove80StartTime;
                if (timeAbove80 >= UPGRADE_REQUIREMENT_TIME && pet.getPetStateObject().canLevelUp()) {
                    upgradePetButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            // Happiness dropped below 80, reset
            if (isHappinessAbove80) {
                isHappinessAbove80 = false;
                happinessAbove80StartTime = 0;
                upgradePetButton.setVisibility(View.GONE);
            }
        }
    }
    
    private void updateLevelDisplay() {
        if (pet != null && petLevel != null) {
            petLevel.setText("Level " + pet.getPetLevel());
        }
        // Also update the pet image when level changes
        updatePetImage();
    }
    
    private void updatePetImage() {
        if (pet == null || petImage == null) return;
        
        int level = pet.getPetLevel();
        int imageResource;
        
        if (pet instanceof Dragon) {
            switch (level) {
                case 1:
                    imageResource = R.drawable.dragon_level1;
                    break;
                case 2:
                    imageResource = R.drawable.dragon_level2;
                    break;
                case 3:
                    imageResource = R.drawable.dragon_level3;
                    break;
                default:
                    imageResource = R.drawable.dragon_level1;
            }
        } else { // Unicorn
            switch (level) {
                case 1:
                    imageResource = R.drawable.unicorn_level1;
                    break;
                case 2:
                    imageResource = R.drawable.unicorn_level2;
                    break;
                case 3:
                    imageResource = R.drawable.unicorn_level3;
                    break;
                default:
                    imageResource = R.drawable.unicorn_level1;
            }
        }
        
        petImage.setImageResource(imageResource);
        Log.d(TAG, "Updated pet image to level " + level);
    }
    
    private void loadPetState() {
        if (pet == null || currentUsername == null) return;
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        PetState petStateObj = pet.getPetStateObject();
        
        // Load saved values (use current values as defaults if nothing saved)
        int savedHappiness = prefs.getInt(currentUsername + KEY_HAPPINESS, petStateObj.getHappinessMeter());
        int savedEnergy = prefs.getInt(currentUsername + KEY_ENERGY, petStateObj.getEnergyMeter());
        int savedHunger = prefs.getInt(currentUsername + KEY_HUNGER, petStateObj.getHungerMeter());
        int savedLevel = prefs.getInt(currentUsername + KEY_LEVEL, petStateObj.getPetLevel());
        long lastSaveTime = prefs.getLong(currentUsername + KEY_LAST_SAVE, System.currentTimeMillis());
        
        // Calculate time elapsed since last save
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastSaveTime;
        long minutesElapsed = timeElapsed / (60 * 1000);
        
        // Apply decay based on time elapsed
        if (minutesElapsed > 0) {
            // Happiness decays by 10 per minute, stops at 20
            savedHappiness = Math.max(20, savedHappiness - (int)(minutesElapsed * 10));
            
            // Energy decays by 5 per minute, stops at 0
            savedEnergy = Math.max(0, savedEnergy - (int)(minutesElapsed * 5));
            
            // Hunger decays by 5 per minute, stops at 1
            savedHunger = Math.max(1, savedHunger - (int)(minutesElapsed * 5));
        }
        
        // Apply the loaded (and decayed) state
        petStateObj.setHappinessMeter(savedHappiness);
        petStateObj.setEnergyMeter(savedEnergy);
        petStateObj.setHungerMeter(savedHunger);
        petStateObj.setPetLevel(savedLevel);
        
        Log.d("PetActivity", "Loaded pet state for " + currentUsername + 
              " - Level: " + savedLevel + ", Happiness: " + savedHappiness + 
              ", Energy: " + savedEnergy + ", Hunger: " + savedHunger +
              " (Minutes elapsed: " + minutesElapsed + ")");
    }
    
    private void savePetState() {
        if (pet == null || currentUsername == null) return;
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        PetState.Meters meters = pet.getPetState();
        
        prefs.edit()
            .putInt(currentUsername + KEY_HAPPINESS, meters.happiness)
            .putInt(currentUsername + KEY_ENERGY, meters.energy)
            .putInt(currentUsername + KEY_HUNGER, meters.hunger)
            .putInt(currentUsername + KEY_LEVEL, pet.getPetLevel())
            .putLong(currentUsername + KEY_LAST_SAVE, System.currentTimeMillis())
            .apply();
        
        // Also save level to database so other activities can access it
        UserRepository userRepository = new UserRepository(this);
        userRepository.updatePetLevel(currentUsername, pet.getPetLevel());
        
        Log.d(TAG, "Saved pet state for " + currentUsername + 
              " - Level: " + pet.getPetLevel() + ", Happiness: " + meters.happiness + 
              ", Energy: " + meters.energy + ", Hunger: " + meters.hunger);
    }

    private void applyMeterDecay() {
        if (pet == null) return;
        
        PetState petStateObj = pet.getPetStateObject();
        PetState.Meters currentMeters = pet.getPetState();
        
        // Decrease happiness by 10 per minute, stop at 20
        if (currentMeters.happiness > 20) {
            int newHappiness = Math.max(20, currentMeters.happiness - 10);
            petStateObj.setHappinessMeter(newHappiness);
        }
        
        // Decrease energy by 5 per minute, stop at 0
        if (currentMeters.energy > 0) {
            int newEnergy = Math.max(0, currentMeters.energy - 5);
            petStateObj.setEnergyMeter(newEnergy);
        }
        
        // Decrease hunger by 5 per minute, stop at 1
        if (currentMeters.hunger > 1) {
            int newHunger = Math.max(1, currentMeters.hunger - 5);
            petStateObj.setHungerMeter(newHunger);
        }
        
        refreshMeters();
        savePetState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the meter decay timer when activity is visible
        if (meterDecayHandler != null && meterDecayRunnable != null) {
            meterDecayHandler.postDelayed(meterDecayRunnable, DECAY_INTERVAL);
        }
        // Start the upgrade check timer
        if (upgradeCheckHandler != null && upgradeCheckRunnable != null) {
            upgradeCheckHandler.postDelayed(upgradeCheckRunnable, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save current pet state before pausing
        savePetState();
        
        // Stop the meter decay timer when activity is not visible
        if (meterDecayHandler != null && meterDecayRunnable != null) {
            meterDecayHandler.removeCallbacks(meterDecayRunnable);
        }
        // Stop the upgrade check timer
        if (upgradeCheckHandler != null && upgradeCheckRunnable != null) {
            upgradeCheckHandler.removeCallbacks(upgradeCheckRunnable);
        }
    }
}
