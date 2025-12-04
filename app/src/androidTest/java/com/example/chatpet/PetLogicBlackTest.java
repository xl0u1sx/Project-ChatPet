package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;

import java.util.concurrent.TimeUnit;


// BLACK BOX TEST CASES FOR TESTING PET ACTIVITY
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetLogicBlackTest {


    @Rule
    public ActivityScenarioRule<PetActivity> activityRule =
            new ActivityScenarioRule<>(PetActivity.class);

    @Before
    public void setUp() {
        // start with a fresh state (Unicorn default)
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("PetActivityPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        // Increase Espresso timeout to handle animations
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS);
        
        // Wait for activity to fully load
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

   // TEST CASE 1: Feeding the pet outputs a message to the user
    @Test
    public void testFeedShowsMessage() {
        // Wait for initial UI to settle
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // Scroll to feed button and click
        onView(withId(R.id.feedButton))
                .perform(scrollTo())
                .perform(click());
        
        // Wait longer for dialog to appear and be interactive
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        
        onView(withText("Pie (+20)")).perform(click());

        // Wait for popup animation to display and dismiss (2 seconds + buffer)
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        // Scroll to statusText before checking visibility
        // After feeding, status text should show a message (even if popup dismissed)
        onView(withId(R.id.statusText))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    // TEST CASE 2: Feeding the pet is blocked when the hunger meter is at MAXIMUM=100
    @Test
    public void testFeedBlockedAtFull() {
        // Wait for initial UI to settle
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // Feed the pet 3 times to fill the hunger meter to 100
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.feedButton))
                    .perform(scrollTo())
                    .perform(click());
            
            // Wait longer for dialog to appear and be interactive
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            
            onView(withText("Roast (+35)")).perform(click());
            
            // Wait for popup animation to complete
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
        }

        // Now hunger should be at 100 (started at 10, added 3x35 = 105, capped at 100)
        // Try to feed a 4th time - this should be blocked and show "not hungry" message
        onView(withId(R.id.feedButton))
                .perform(scrollTo())
                .perform(click());
        
        // Wait a moment for status text to update
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // Now we should see the "not hungry" message
        onView(withId(R.id.statusText))
                .perform(scrollTo())
                .check(matches(withText(containsString("not hungry"))));
    }



   // TEST CASE 3: Tucking in the pet is blocked when the pet is already asleep
    @Test
    public void testTuckInCooldownMessage() {
        // Wait for initial UI to settle
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // First tuck-in
        onView(withId(R.id.tuckInButton))
                .perform(scrollTo())
                .perform(click());
        
        // Wait for popup to display and dismiss
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        
        // Second tuck-in (should be blocked)
        onView(withId(R.id.tuckInButton))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.statusText))
                .perform(scrollTo())
                .check(matches(withText(containsString(
                        "doesn't want to go to sleep yet"))));
    }

    // TEST CASE 4: Feeding the pet is blocked when it is asleep
    @Test
    public void testFeedBlockedWhileAsleep() {
        // Wait for initial UI to settle
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // Tuck in the pet
        onView(withId(R.id.tuckInButton))
                .perform(scrollTo())
                .perform(click());
        
        // Wait for popup to display and dismiss
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        
        // Try to feed (should be blocked)
        onView(withId(R.id.feedButton))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.statusText))
                .perform(scrollTo())
                .check(matches(withText(containsString(
                        "is asleep right now. You can't feed them"))));
    }

    // TEST CASE 5: The pet Action button shouldn't do anything when the pet is asleep
    // This action should output a message to the user indicating so
    @Test
    public void testSpecialActionBlockedWhileAsleep() {
        // Wait for initial UI to settle
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // Tuck in the pet first
        onView(withId(R.id.tuckInButton))
                .perform(scrollTo())
                .perform(click());

        // Wait for popup to display and dismiss
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        // Try to perform special action (should be blocked)
        onView(withId(R.id.tellStoryButton))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.statusText))
                .perform(scrollTo())
                .check(matches(withText(containsString(
                        "is asleep right now. You can't hear a story"
                ))));
    }

}
