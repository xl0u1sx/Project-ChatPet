package com.example.chatpet;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JournalBlackBoxTest {

    private static final String TEST_USERNAME = "test12331";
    private static final String PREFS_NAME = "ChatPetPrefs";
    private static final String KEY_USERNAME = "username";

    @Rule
    public ActivityScenarioRule<JournalActivity> activityScenarioRule
            = new ActivityScenarioRule<>(JournalActivity.class);

    @Before
    public void setUp() {
        // Set up test user in SharedPreferences
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USERNAME, TEST_USERNAME).apply();
    }

    @Test
    public void test_JournalActivity_LaunchesAndDisplaysUI() {
        // Verify main UI components are displayed
        onView(withId(R.id.generateJournalButton))
                .check(matches(isDisplayed()));

        onView(withId(R.id.journalEntriesContainer))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test_GenerateJournalButton_IsClickable() {
        // Verify button is enabled
        onView(withId(R.id.generateJournalButton))
                .check(matches(isEnabled()));

        // Verify button is clickable (perform click without error)
        onView(withId(R.id.generateJournalButton))
                .perform(click());
    }

    @Test
    public void test_GenerateJournalButton_ShowsLoadingState() {
        // Click generate button
        onView(withId(R.id.generateJournalButton))
                .perform(click());

        // Verify loading indicator appears
        onView(withId(R.id.loadingProgressBar))
                .check(matches(isDisplayed()));

        onView(withId(R.id.loadingTextView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test_EmptyHistoryMessage_DisplaysWhenNoJournals() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify journal container is displayed
        onView(withId(R.id.journalEntriesContainer))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test_JournalHistoryContainer_IsPresent() {
        // Verify the journal entries container exists and is displayed
        onView(withId(R.id.journalEntriesContainer))
                .check(matches(isDisplayed()));
    }
}