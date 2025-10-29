package com.example.chatpet;

import android.content.Intent;
import android.os.Bundle;
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
    private ProgressBar happinessProg;
    private ProgressBar energyProg;
    private ProgressBar hungerProg;
    private Button petAcitivityButton; // keep your variable name
    private Button breatheFireButton;
    private Button tellStoryButton;
    private ImageView petImage;
    private TextView statusText;

    // Intent keys (as you defined)
    public static final String temp_user_id = "temp_user_id";
    public static final String temp_pet_name = "temp_pet_name";
    public static final String temp_pet_type = "temp_pet_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        getViews();
        initFromIntent(getIntent());
        setupButtons();
        refreshMeters();
    }

    private void getViews() {
        petTitle = findViewById(R.id.petTitle);
        petType = findViewById(R.id.petType);
        petName = findViewById(R.id.petName);
        happinessProg = findViewById(R.id.happinessProg);
        energyProg = findViewById(R.id.energyProg);
        hungerProg = findViewById(R.id.hungerProg);
        petAcitivityButton = findViewById(R.id.petActivityButton);
        breatheFireButton = findViewById(R.id.breatheFireButton);
        tellStoryButton = findViewById(R.id.tellStoryButton);
        petImage = findViewById(R.id.petImage);
        statusText = findViewById(R.id.statusText);

        // progress bars
        if (happinessProg != null) happinessProg.setMax(100);
        if (energyProg != null)    energyProg.setMax(100);
        if (hungerProg != null)    hungerProg.setMax(100);
    }

    private void initFromIntent(@Nullable Intent intent) {
        // Read extras with safe defaults
        String type, name, uid;

        if(intent!=null){
            type=intent.getStringExtra(temp_pet_type);
            name=intent.getStringExtra(temp_pet_name);
            uid=intent.getStringExtra(temp_user_id);
        }
        else{
            type="Unicorn";
            name="Pet";
            uid="temp_user_id";
        }
//        String type  = intent != null ? intent.getStringExtra(temp_pet_type) : null;
//        String name = intent != null ? intent.getStringExtra(temp_pet_name)     : null;
//        String uid  = intent != null ? intent.getStringExtra(temp_user_id)      : null;
//
//        if (type == null  || type.isEmpty())  type  = "Unicorn";
//        if (name == null || name.isEmpty()) name = "Pet";
//        if (uid == null  || uid.isEmpty())  uid  = "temp_user_id";

        // Your PetService signature is (petCategory, petName, userID)
        pet = petService.createPet(type, name, uid);

        if (petTitle != null) petTitle.setText("Your Pet");
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

        // Toggle special buttons
        if(pet!=null){
            if(pet.getPetType().equals("Dragon")){
                if (breatheFireButton != null) breatheFireButton.setVisibility(View.VISIBLE);
                if (tellStoryButton != null)   tellStoryButton.setVisibility(View.GONE);
            }
            else{
                if (breatheFireButton != null) breatheFireButton.setVisibility(View.GONE);
                if (tellStoryButton != null)   tellStoryButton.setVisibility(View.VISIBLE);
            }

        }
//        boolean isDragon = pet != null && "Dragon".equalsIgnoreCase(pet.getPetType());
//        if (breatheFireButton != null) breatheFireButton.setVisibility(isDragon ? View.VISIBLE : View.GONE);
//        if (tellStoryButton != null)   tellStoryButton.setVisibility(isDragon ? View.GONE : View.VISIBLE);

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
        if (petAcitivityButton != null) {
            petAcitivityButton.setOnClickListener(v -> {
                if (pet == null) return;
                String msg = pet.petActivity();
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
            });
        }

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
                String msg = ((Unicorn) pet).tellMagicalStory(); // matches your Unicorn API
                if (statusText != null) statusText.setText(msg);
                refreshMeters();
            });
        }
    }
}
