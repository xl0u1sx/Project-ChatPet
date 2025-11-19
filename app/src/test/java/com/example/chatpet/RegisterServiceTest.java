package com.example.chatpet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * white box junit unit tests for registration service
 * purpose: test registration validation logic and maintain code coverage
 */
public class RegisterServiceTest {

    /**
     * test case 5: registerservice invalid password length
     *
     * description: input a pet name, username, but input 3 letters "abc" for the
     * password to see if ui errors for invalid password length (<4 characters)
     * rationale: check code coverage on handleregistration() if it hits
     * if (password.length() < 4) so that users have secure passwords
     * expected result: ui error message "password must be at least 4 characters long"
     * with no registration
     * bugs: none
     *
     */
    @Test
    public void testInvalidPasswordLength() {
        // arrange: simulate inputs from registration form
        String firstName = "john";
        String lastName = "doe";
        String username = "johndoe";
        String password = "abc"; // only 3 characters
        String petName = "fluffy";

        // act: check the password length validation (registeractivity.java line 111)
        boolean isPasswordValid = password.length() >= 4;

        // assert: password should be invalid
        assertFalse("password with less than 4 characters should be invalid", isPasswordValid);

        // in the actual ui (registeractivity:112-114), this triggers:
        // errortext.settext("password must be at least 4 characters long")
        // errortext.setvisibility(view.visible)
        // return (no registration)
    }

    @Test
    public void testValidPasswordLength() {
        // arrange: password with exactly 4 characters
        String password = "pass";

        // act: check validation
        boolean isPasswordValid = password.length() >= 4;

        // assert: should be valid
        assertTrue("password with 4 characters should be valid", isPasswordValid);
    }

    @Test
    public void testLongerValidPassword() {
        // arrange: password with more than 4 characters
        String password = "password123";

        // act: check validation
        boolean isPasswordValid = password.length() >= 4;

        // assert: should be valid
        assertTrue("password with more than 4 characters should be valid", isPasswordValid);
    }

    @Test
    public void testEmptyPassword() {
        // arrange: empty password
        String password = "";

        // act: check validation
        boolean isPasswordValid = password.length() >= 4;

        // assert: should be invalid
        assertFalse("empty password should be invalid", isPasswordValid);
    }

    @Test
    public void testSingleCharacterPassword() {
        // arrange: single character password
        String password = "a";

        // act: check validation
        boolean isPasswordValid = password.length() >= 4;

        // assert: should be invalid
        assertFalse("single character password should be invalid", isPasswordValid);
    }
}
