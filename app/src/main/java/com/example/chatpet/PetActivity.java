package com.example.chatpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PetActivity extends AppCompatActivity {

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
    private Button chatButton;
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
    
    // SharedPreferences for tracking tuck-in cooldown
    private static final String PREFS_NAME = "PetActivityPrefs";
    private static final String KEY_LAST_TUCK_IN = "lastTuckInTime";
    private static final long TUCK_IN_COOLDOWN = 20 * 60 * 1000; // 20 minutes in milliseconds (for demo)
    
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
        chatButton = findViewById(R.id.chatButton);
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

        pet = petService.createPet(type, name, uid);

        if (petTitle != null) petTitle.setText("");
        if (pet != null) {
            if (petType != null) petType.setText(pet.getPetType());
            if (petName != null)     petName.setText(pet.getPetName());
        }

        if (petImage != null) {
            if (pet instanceof Dragon){
                petImage.setImageResource(R.drawable.dragon);
            }
            else{
                petImage.setImageResource(R.drawable.unicorn);
            }

        }


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
        // Chat button - increases happiness by 15
        if (chatButton != null) {
            chatButton.setOnClickListener(v -> {
                if (pet == null) return;
                String msg = pet.chat();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
            });
        }

        // Feed button - increases hunger by 30, increase energy by 10
        if (feedButton != null) {
            feedButton.setOnClickListener(v -> {
                if (pet == null) return;
                String msg = pet.feed();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
            });
        }

        // Tuck-in button - fills energy to 100, but only once per 24 hours
        if (tuckInButton != null) {
            tuckInButton.setOnClickListener(v -> {
                if (pet == null) return;
                
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                long lastTuckInTime = prefs.getLong(KEY_LAST_TUCK_IN, 0);
                long currentTime = System.currentTimeMillis();
                long timeSinceLastTuckIn = currentTime - lastTuckInTime;
                
                if (timeSinceLastTuckIn >= TUCK_IN_COOLDOWN) {
                    // Tuck-in is available
                    String msg = pet.tuckIn();
                    prefs.edit().putLong(KEY_LAST_TUCK_IN, currentTime).apply();
                    if (statusText != null) statusText.setText(msg);
                    refreshMeters();
                } else {
                    // Still on cooldown
                    long remainingTime = TUCK_IN_COOLDOWN - timeSinceLastTuckIn;
                    long hoursRemaining = remainingTime / (60 * 60 * 1000);
                    long minutesRemaining = (remainingTime % (60 * 60 * 1000)) / (60 * 1000);
                    String cooldownMsg = pet.getPetName() + " is still resting! Please wait " + 
                                       hoursRemaining + " hours and " + minutesRemaining + " minutes.";
                    if (statusText != null) statusText.setText(cooldownMsg);
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
            });
        }

        if (tellStoryButton != null) {
            tellStoryButton.setOnClickListener(v -> {
                if (!(pet instanceof Unicorn)) return;
                String msg = ((Unicorn) pet).tellMagicalStory();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
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
