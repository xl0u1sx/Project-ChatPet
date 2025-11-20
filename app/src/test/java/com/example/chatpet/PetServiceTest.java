package com.example.chatpet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * white box junit unit tests for pet service
 * purpose: test internal pet service logic including feeding and hunger meter
 */
public class PetServiceTest {

    private Pet testPet;
    private PetState petState;

    @Before
    public void setUp() {
        // create a test pet with initial state
        petState = new PetState("Unicorn", "testuser", "testpet");
        testPet = new Unicorn(petState);
    }

    /**
     * test case 3: petservice feed increases hunger
     *
     * description: call feed() on the pet and check the hunger meter value
     * rationale: test if the feed method correctly increases the hunger level
     * and caps it at the maximum value
     * expected result: hunger meter increases but does not exceed max
     * bugs: none
     */
    @Test
    public void testFeedIncreasesHunger() {
        // arrange: get initial hunger value
        int initialHunger = petState.getHungerMeter();

        // act: feed the pet
        testPet.feed();

        // assert: hunger should increase
        int newHunger = petState.getHungerMeter();
        assertTrue("hunger should increase after feeding", newHunger > initialHunger);

        // verify hunger doesn't exceed max value (100)
        assertTrue("hunger should not exceed 100", newHunger <= 100);
    }

    /**
     * additional test: verify hunger caps at maximum
     *
     * description: feed the pet multiple times to test the cap
     * rationale: ensure the hunger meter cannot exceed the maximum value of 100
     * expected result: hunger stays at 100 even after multiple feeds
     * bugs: none
     */
    @Test
    public void testFeedCapsAtMaximum() {
        // arrange: feed the pet multiple times to reach max
        for (int i = 0; i < 10; i++) {
            testPet.feed();
        }

        // assert: hunger should be capped at 100
        int hunger = petState.getHungerMeter();
        assertEquals("hunger should cap at 100", 100, hunger);
    }

    //WB4
    @Test
    public void test_CreatePet_TypeNormalization(){
        PetService service = new PetService();

        Pet pet = service.createPet("DRAGON", "Darg", "testUser");

        assertTrue("Type normalization should result in Dragon", pet instanceof Dragon);
    }
}
