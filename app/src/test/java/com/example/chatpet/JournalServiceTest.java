package com.example.chatpet;

import org.junit.Test;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import java.util.List;
import java.util.ArrayList;

public class JournalServiceTest {

    // White box test 1: Testing formatPrompt with valid data
    @Test
    public void test_FormatPrompt_WithValidData() {
        System.out.println("\n=== TEST 1 STARTED: test_FormatPrompt_WithValidData ===");

        JournalEntry entry = new JournalEntry();
        entry.petType = "Dragon";
        entry.petName = "Fluffy";
        entry.petLevel = 2;
        entry.levelProgress = 75;
        entry.expGained = 25;
        entry.date = "November 18, 2025";
        entry.time = "03:45 PM";
        entry.happiness = 80;
        entry.energy = 90;
        entry.hunger = 60;
        entry.timesChatted = 5;
        entry.timesFed = 3;
        entry.timesTuckedIn = 2;

        String previousJournal = "Yesterday was great!";
        String prompt = JournalService.formatPrompt(entry, previousJournal);

        System.out.println("Generated Prompt:");
        System.out.println(prompt);

        // Updated for new shorter prompt format
        assertNotNull(prompt);
        assertTrue(prompt.contains("Dragon"));
        assertTrue(prompt.contains("Fluffy"));
        assertTrue(prompt.contains("level 2"));
        assertTrue(prompt.contains("Happiness: 80"));
        assertTrue(prompt.contains("Yesterday was great!"));

        System.out.println("TEST 1 PASSED\n");
    }

    // White box test 2: Testing formatPrompt isEmpty() branch with empty string - first journal entry
    @Test
    public void test_FormatPrompt_WithEmptyPreviousJournal() {
        System.out.println("\n=== TEST 2 STARTED: test_FormatPrompt_WithEmptyPreviousJournal ===");

        JournalEntry entry = new JournalEntry();
        entry.petType = "Cat";
        entry.petName = "Whiskers";
        entry.petLevel = 1;
        entry.levelProgress = 10;
        entry.expGained = 5;
        entry.date = "November 18, 2025";
        entry.time = "10:00 AM";
        entry.happiness = 50;
        entry.energy = 100;
        entry.hunger = 100;
        entry.timesChatted = 0;
        entry.timesFed = 1;
        entry.timesTuckedIn = 0;

        String previousJournal = "";
        String prompt = JournalService.formatPrompt(entry, previousJournal);

        System.out.println("Generated Prompt:");
        System.out.println(prompt);

        // Updated for new shorter prompt format
        assertNotNull(prompt);
        // Should be "First entry" instead of empty string for empty previous journal
        assertTrue(prompt.contains("First entry"));
        assertTrue(prompt.contains("Cat"));
        assertTrue(prompt.contains("Whiskers"));
        assertTrue(prompt.contains("level 1"));
        assertTrue(prompt.contains("Happiness: 50"));
        assertTrue(prompt.contains("Energy: 100"));
        assertTrue(prompt.contains("Hunger: 100"));
        assertTrue(prompt.contains("Chats: 0"));
        assertTrue(prompt.contains("Fed: 1"));
        assertTrue(prompt.length() > 50);

        System.out.println("TEST 2 PASSED\n");
    }

    // White box test 3: Testing JournalEntry's default constructor
    @Test
    public void test_JournalEntry_Default_Constructor() {
        System.out.println("\n=== TEST 3 STARTED: test_JournalEntry_Default_Constructor ===");

        JournalEntry entry = new JournalEntry();

        System.out.println("Entry ID: " + entry.getEntryId());
        System.out.println("Date: " + entry.getDate());
        System.out.println("Time: " + entry.getTime());
        System.out.println("Happiness: " + entry.getHappiness());
        System.out.println("Energy: " + entry.getEnergy());
        System.out.println("Hunger: " + entry.getHunger());
        System.out.println("Pet Type: " + entry.getPetType());
        System.out.println("Pet Name: " + entry.getPetName());

        assertNotNull(entry.getEntryId());
        assertNotNull(entry.getDate());
        assertNotNull(entry.getTime());
        assertTrue(entry.getHappiness() >= 1 && entry.getHappiness() <= 100);
        assertTrue(entry.getEnergy() >= 1 && entry.getEnergy() <= 100);
        assertTrue(entry.getHunger() >= 1 && entry.getHunger() <= 100);
        assertEquals("Dragon", entry.getPetType());
        assertEquals("Fluffy", entry.getPetName());

        System.out.println("TEST 3 PASSED\n");
    }

    // White box test 4: Testing JournalEntry getters and setters
    @Test
    public void test_JournalEntry_GettersAndSetters() {
        System.out.println("\n=== TEST 4 STARTED: test_JournalEntry_GettersAndSetters ===");

        JournalEntry entry = new JournalEntry();

        entry.setUsername("testUser");
        entry.setJournalText("Today was amazing!");

        System.out.println("Username: " + entry.getUsername());
        System.out.println("Journal Text: " + entry.getJournalText());
        System.out.println("Date: " + entry.getDate());
        System.out.println("Time: " + entry.getTime());

        assertEquals("testUser", entry.getUsername());
        assertEquals("Today was amazing!", entry.getJournalText());
        assertNotNull(entry.getDate());
        assertNotNull(entry.getTime());

        System.out.println("TEST 4 PASSED\n");
    }

    // White box test 5: Testing JournalEntry database constructor
    @Test
    public void test_JournalEntry_DatabaseConstructor() {
        System.out.println("\n=== TEST 5 STARTED: test_JournalEntry_DatabaseConstructor ===");

        JournalEntry entry = new JournalEntry(
                1,
                "Sample journal text",
                "November 18, 2025",
                "02:30 PM",
                "testUser"
        );

        System.out.println("DB Entry ID: " + entry.dbEntryId);
        System.out.println("Journal Text: " + entry.getJournalText());
        System.out.println("Date: " + entry.getDate());
        System.out.println("Time: " + entry.getTime());
        System.out.println("Username: " + entry.getUsername());

        assertEquals(1, entry.dbEntryId);
        assertEquals("Sample journal text", entry.getJournalText());
        assertEquals("November 18, 2025", entry.getDate());
        assertEquals("02:30 PM", entry.getTime());
        assertEquals("testUser", entry.getUsername());

        System.out.println("TEST 5 PASSED\n");
    }


    //WB 1 - check for no "Okay" at start of entry
    @Test
    public void test_FormatPrompt_DoesNotStartWithOkay() {
        JournalEntry entry = new JournalEntry();
        entry.petType = "Unicorn";
        entry.petName = "Grey";
        entry.petLevel = 3;
        entry.levelProgress = 55;
        entry.expGained = 10;
        entry.date = "Nov 19, 2025";
        entry.time = "9:00 AM";
        entry.happiness = 70;
        entry.energy = 80;
        entry.hunger = 90;
        entry.timesChatted = 2;
        entry.timesFed = 1;
        entry.timesTuckedIn = 0;

        String prompt = JournalService.formatPrompt(entry, "");
        assertFalse(prompt.trim().startsWith("Okay"));
    }

    //empty previous journal
    @Test
    public void testFormatPrompt_InsertsNoneForEmptyPreviousJournal() {
        JournalEntry entry = new JournalEntry();

        entry.petType = "Dog";
        entry.petName = "Fido";

        String result = JournalService.formatPrompt(entry, "");

        // Updated for new shorter prompt format - now uses "First entry" instead of "None"
        assertTrue(result.contains("First entry"));
    }


}