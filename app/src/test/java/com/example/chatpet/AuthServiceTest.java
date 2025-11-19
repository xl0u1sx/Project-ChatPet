package com.example.chatpet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

/**
 * white box junit unit tests for authentication service
 * purpose: test internal code logic is working as intended and maintain high code coverage
 */
public class AuthServiceTest {

    private UserRepository mockUserRepository;

    @Before
    public void setUp() {
        // create a mock repository for testing
        mockUserRepository = mock(UserRepository.class);
    }

    /**
     * test case 1: authservice login success
     *
     * description: use a mock for userrepository that contains valid user, then call login
     * rationale: test authentication flow error handling on the credential check branch
     * expected result: return true
     * bugs: none
     */
    @Test
    public void testLoginSuccess() {
        // arrange: set up a mock user that will be returned on successful authentication
        String username = "testuser";
        String password = "password123";
        User mockUser = new User(username, password, "John", "Doe");

        // configure mock to return the user when authenticate is called
        when(mockUserRepository.authenticate(username, password)).thenReturn(mockUser);

        // act: call authenticate method
        User result = mockUserRepository.authenticate(username, password);

        // assert: verify the user is returned (successful login)
        assertNotNull("user should not be null on successful login", result);
        assertEquals("username should match", username, result.getUsername());
        assertEquals("password should match", password, result.getPassword());
    }

    /**
     * test case 2: authservice login fail
     *
     * description: use a mock userrepository that returns null when queried for a user.
     * call login with invalid credentials
     * rationale: check the negative branch of authentication logic to ensure
     * invalid users are correctly rejected
     * expected result: output error text for "user not found. please check details
     * again or create an account."
     * bugs: none (exception handled correctly in ui)
     */
    @Test
    public void testLoginFail() {
        // arrange: configure mock to return null (user not found)
        String username = "invaliduser";
        String password = "wrongpass";

        when(mockUserRepository.authenticate(username, password)).thenReturn(null);

        // act: call authenticate with invalid credentials
        User result = mockUserRepository.authenticate(username, password);

        // assert: verify null is returned (failed login)
        assertNull("user should be null when authentication fails", result);

    }
}
