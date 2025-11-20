package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Black box tests for core pet interactions.
 * Updated to match current Unicorn/Dragon + PetActivity behavior.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetLogicBlackTest {

    // Launches PetActivity with default intent:
    // type = "Unicorn", name = "Pet", uid = "temp_user_id"
    @Rule
    public ActivityScenarioRule<PetActivity> activityRule =
            new ActivityScenarioRule<>(PetActivity.class);

    /**
     * Test Case 1: Feeding gives visible feedback.
     */
    @Test
    public void testFeedShowsMessage() {
        onView(withId(R.id.feedButton)).perform(click());
        onView(withText("Pie (+20)")).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(isDisplayed()));
    }

    /**
     * Test Case 2: Feeding blocked when full.
     *
     * Before feeding, we clear the tuck-in timestamp so the pet
     * is NOT considered "asleep" from a previous test run.
     */
    @Test
    public void testFeedBlockedAtFull() {
        // Ensure tuck-in / sleep cooldown state is reset for this test
        activityRule.getScenario().onActivity(activity -> {
            SharedPreferences prefs =
                    activity.getSharedPreferences("PetActivityPrefs", Context.MODE_PRIVATE);
            // KEY is currentUsername + "_lastTuckInTime" -> default username is "temp_user_id"
            prefs.edit()
                    .putLong("temp_user_id_lastTuckInTime", 0L)
                    .apply();
        });

        // Spam feed until hunger reaches 100 (full)
        for (int i = 0; i < 6; i++) {
            onView(withId(R.id.feedButton)).perform(click());
            onView(withText("Pie (+20)")).perform(click());

        }

        // Now we should see the "already full" message
        onView(withId(R.id.statusText))
                .check(matches(withText(containsString("already full"))));
    }

    /**
     * Test Case 3: Tuck-in cooldown shows a "please wait" message.
     */
    @Test
    public void testTuckInCooldownMessage() {
        onView(withId(R.id.tuckInButton)).perform(click());
        onView(withId(R.id.tuckInButton)).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(withText(containsString(
                        "doesn't want to go to sleep yet! Please wait"))));
    }

    /**
     * Test Case 4: Feeding is blocked while pet is asleep.
     */
    @Test
    public void testFeedBlockedWhileAsleep() {
        onView(withId(R.id.tuckInButton)).perform(click());
        onView(withId(R.id.feedButton)).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(withText(
                        "Pet is asleep right now. You can't feed them until they wake up!")));
    }

    /**
     * Test Case 5: Special action is blocked while pet is asleep.
     */
    @Test
    public void testSpecialActionBlockedWhileAsleep() {
        onView(withId(R.id.tuckInButton)).perform(click());
        onView(withId(R.id.tellStoryButton)).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(withText(
                        "Pet is asleep right now. You can't hear a story while they're asleep until they wake up!"
                )));
    }
}
