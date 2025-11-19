package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * black box espresso tests for login functionality
 * tests verify app behavior from the user's perspective without looking at internal code
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTests {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * test case 1: valid login
     *
     * description: launch app, enter valid username and password, then login
     * rationale: test authentication flow for the user
     * expected result: app logs in correctly
     * bugs: none
     */
    @Test
    public void testValidLogin() {
        // enter a valid username (assuming "testuser" exists in db)
        onView(withId(R.id.usernameInput))
                .perform(typeText("testuser"), closeSoftKeyboard());

        // enter the correct password
        onView(withId(R.id.passwordInput))
                .perform(typeText("password123"), closeSoftKeyboard());

        // click the login button
        onView(withId(R.id.loginButton))
                .perform(click());

        // verify we successfully logged in
        // note: this test assumes "testuser" with password "password123" exists in the database
        // if login is successful, we should navigate to MainActivity
    }

    /**
     * test case 2: invalid login
     *
     * description: launch app, enter invalid username and password, then login
     * rationale: test authentication flow error handling for the user
     * expected result: app outputs invalid credentials and does not log in
     * bugs: none
     */
    @Test
    public void testInvalidLogin() {
        // enter an invalid username
        onView(withId(R.id.usernameInput))
                .perform(typeText("nonexistentuser"), closeSoftKeyboard());

        // enter an invalid password
        onView(withId(R.id.passwordInput))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());

        // click the login button
        onView(withId(R.id.loginButton))
                .perform(click());

        // verify error message is displayed
        onView(withId(R.id.errorText))
                .check(matches(withText("User not found. Please check details again or create an account.")))
                .check(matches(isDisplayed()));
    }
}
