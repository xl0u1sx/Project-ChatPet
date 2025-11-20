package com.example.chatpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Black-box Instrumented Test for Pet Meter Display
 * 
 * Test Type: UI Display Verification
 * 
 * This black-box test verifies that:
 * 1. All three meters (Happiness, Energy, Hunger) are visible
 * 2. Pet level is displayed correctly
 * 3. Meter values are loaded from SharedPreferences and displayed
 * 4. UI elements render properly
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetMeterDisplayTest {
    
    private Context context;
    
    @Rule
    public ActivityScenarioRule<PetActivity> activityRule = 
        new ActivityScenarioRule<>(createPetActivityIntent());
    
    private static Intent createPetActivityIntent() {
        Context context = ApplicationProvider.getApplicationContext();
        
        // Set up test data in SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("PetActivityPrefs", Context.MODE_PRIVATE);
        prefs.edit()
            .putInt("testUser_happiness", 75)
            .putInt("testUser_energy", 60)
            .putInt("testUser_hunger", 80)
            .putInt("testUser_level", 2)
            .putLong("testUser_lastSave", System.currentTimeMillis())
            .apply();
        
        Intent intent = new Intent(context, PetActivity.class);
        intent.putExtra(PetActivity.temp_user_id, "testUser");
        intent.putExtra(PetActivity.temp_pet_type, "Dragon");
        intent.putExtra(PetActivity.temp_pet_name, "TestDragon");
        return intent;
    }
    
    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }
    
    /**
     * Test Case 1: Verify All Meters Are Displayed
     * Tests that happiness, energy, and hunger progress bars are visible
     * Input: Navigate to Pet Activity
     * Expected: All three progress bars should be displayed
     */
    @Test
    public void testAllMetersAreDisplayed() {
        onView(withId(R.id.happinessProg)).check(matches(isDisplayed()));
        onView(withId(R.id.energyProg)).check(matches(isDisplayed()));
        onView(withId(R.id.hungerProg)).check(matches(isDisplayed()));
    }
    
    /**
     * Test Case 2: Verify Pet Level Is Displayed
     * Tests that pet level TextView shows correct level
     * Input: Pet at level 2
     * Expected: "Level 2" text should be displayed
     */
    @Test
    public void testPetLevelIsDisplayed() {
        onView(withId(R.id.petLevel))
            .check(matches(isDisplayed()))
            .check(matches(withText("Level 2")));
    }
    
    /**
     * Test Case 3: Verify Pet Name and Type Display
     * Tests that pet information is displayed correctly
     * Input: Dragon named "TestDragon"
     * Expected: Name and type labels should show correct values
     */
    @Test
    public void testPetNameAndTypeDisplay() {
        onView(withId(R.id.petName))
            .check(matches(isDisplayed()))
            .check(matches(withText("TestDragon")));
        
        onView(withId(R.id.petType))
            .check(matches(isDisplayed()))
            .check(matches(withText("Dragon")));
    }
    
    /**
     * Test Case 4: Verify Meter Labels Are Visible
     * Tests that meter section labels are displayed
     * Input: Open Pet Activity
     * Expected: "Pet Care Meters" and "Pet Actions" labels visible
     */
    @Test
    public void testMeterSectionLabelsVisible() {
        onView(withId(R.id.sectionTitleMeters))
            .check(matches(isDisplayed()))
            .check(matches(withText("Pet Care Meters")));
        
        onView(withId(R.id.actionsTitle))
            .check(matches(isDisplayed()))
            .check(matches(withText("Pet Actions")));
    }
    
    /**
     * Test Case 5: Verify Pet Image Is Displayed
     * Tests that pet image renders correctly
     * Input: Dragon pet type
     * Expected: Pet image should be visible
     */
    @Test
    public void testPetImageIsDisplayed() {
        onView(withId(R.id.petImage))
            .check(matches(isDisplayed()));
    }
}

