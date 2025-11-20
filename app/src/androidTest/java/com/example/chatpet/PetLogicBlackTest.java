package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// BLACK BOX TEST CASES FOR TESTING PET ACTIVITY
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PetLogicBlackTest {


    @Rule
    public ActivityScenarioRule<PetActivity> activityRule =
            new ActivityScenarioRule<>(PetActivity.class);

   // TEST CASE 1: Feeding the pet outputs a message to the user
    @Test
    public void testFeedShowsMessage() {
        onView(withId(R.id.feedButton)).perform(click());
        onView(withText("Pie (+20)")).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(isDisplayed()));
    }

    // TEST CASE 2: Feeding the pet is blocked when the hunger meter is at MAXIMUM=100
    @Test
    public void testFeedBlockedAtFull() {

        activityRule.getScenario().onActivity(activity -> {
            SharedPreferences prefs =
                    activity.getSharedPreferences("PetActivityPrefs", Context.MODE_PRIVATE);

            prefs.edit()
                    .putLong("temp_user_id_lastTuckInTime", 0L)
                    .apply();
        });


        for (int i = 0; i < 6; i++) {
            onView(withId(R.id.feedButton)).perform(click());
            onView(withText("Pie (+20)")).perform(click());

        }

        // Now we should see the "already full" message
        onView(withId(R.id.statusText))
                .check(matches(withText(containsString("already full"))));
    }

   // TEST CASE 3: Tucking in the pet is blocked when the pet is already asleep
    @Test
    public void testTuckInCooldownMessage() {
        onView(withId(R.id.tuckInButton)).perform(click()); // tucks in the pet
        onView(withId(R.id.tuckInButton)).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(withText(containsString(
                        "doesn't want to go to sleep yet! Please wait"))));
    }

    // TEST CASE 4: Feeding the pet is blocked when it is asleep
    @Test
    public void testFeedBlockedWhileAsleep() {
        onView(withId(R.id.tuckInButton)).perform(click()); // tucks in the pet
        onView(withId(R.id.feedButton)).perform(click()); // user clicks on the feed button

        onView(withId(R.id.statusText))
                .check(matches(withText(
                        "Pet is asleep right now. You can't feed them until they wake up!")));
    }

    // TEST CASE 5: The pet Action button shouldn't do anything when the pet is asleep
    // This action should output a message to the user indicating so
    @Test
    public void testSpecialActionBlockedWhileAsleep() {
        onView(withId(R.id.tuckInButton)).perform(click());
        onView(withId(R.id.tellStoryButton)).perform(click());

        onView(withId(R.id.statusText))
                .check(matches(withText(
                        "Pet is asleep right now. You can't hear a story while they're asleep until they wake up!"
                )));
    }

    //LL
    @Test
    public void testStoryMenuAppears() {
        onView(withId(R.id.tellStoryButton)).perform(click());

        onView(withText("Short Story"))
                .check(matches(isDisplayed()));
    }


    // clicking feed shows status update
    @Test
    public void test_FeedButton_Clickable() {
        onView(withId(R.id.feedButton)).perform(click());
        onView(withId(R.id.statusText)).check(matches(isDisplayed()));
    }

    // clicking tuck-in shows status update
    @Test
    public void test_TuckInButton_Clickable() {
        onView(withId(R.id.tuckInButton)).perform(click());
        onView(withId(R.id.statusText)).check(matches(isDisplayed()));
    }

}
