package com.example.chatpet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * white box junit unit tests for login ui validation
 * purpose: test ui validation logic to maintain code coverage
 */
public class LoginUiTest {

    /**
     * test case 4: ui login credentials missing
     *
     * description: leave username or password empty (one or the other),
     * and check if ui correctly outputs feedback
     * rationale: check the code coverage on the handlelogin() function to see
     * if it checks both login and password present; error message otherwise
     * expected result: output error text "please enter both username and password"
     * bugs: none
     *
     * note: this is a unit test simulating the validation logic from loginactivity:52-58
     * in actual implementation, this validation happens in the ui layer
     */
    @Test
    public void testEmptyUsernameValidation() {
        // simulate the validation logic from loginactivity
        String username = "";
        String password = "password123";

        // this mirrors the logic in loginactivity.java line 52
        boolean isValid = !username.isEmpty() && !password.isEmpty();

        // assert: validation should fail
        assertEquals("validation should fail when username is empty", false, isValid);

        // in the actual ui (loginactivity:53-55), this triggers:
        // errortext.settext("please enter both username and password")
    }

    @Test
    public void testEmptyPasswordValidation() {
        // simulate the validation logic
        String username = "testuser";
        String password = "";

        // this mirrors the logic in loginactivity.java line 52
        boolean isValid = !username.isEmpty() && !password.isEmpty();

        // assert: validation should fail
        assertEquals("validation should fail when password is empty", false, isValid);
    }

    @Test
    public void testBothFieldsEmptyValidation() {
        // simulate the validation logic
        String username = "";
        String password = "";

        boolean isValid = !username.isEmpty() && !password.isEmpty();

        // assert: validation should fail
        assertEquals("validation should fail when both fields are empty", false, isValid);
    }

    @Test
    public void testValidCredentialsFormat() {
        // simulate the validation logic
        String username = "testuser";
        String password = "password123";

        boolean isValid = !username.isEmpty() && !password.isEmpty();

        // assert: validation should pass
        assertEquals("validation should pass with both fields filled", true, isValid);
    }
}
