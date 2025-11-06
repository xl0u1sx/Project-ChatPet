package com.example.chatpet;

import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.UUID;

public class JournalEntry {
    int dbEntryId; // Database entry_id
    String entryId; // UUID for temporary entries
    String date;
    String time;
    String journalText;
    String username;
    int petLevel;
    String petType;
    String petName;
    int happiness;
    int energy;
    int hunger;
    int timesChatted;
    int timesFed;
    int timesTuckedIn;
    int levelProgress;
    int expGained;

    // Constructor for creating a new journal entry (for generation)
    public JournalEntry()
    {
        entryId = UUID.randomUUID().toString();

        // using random for meters until database and other classes are implemented
        Random random = new Random();

        // actual date/time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        date = currentDateTime.format(dateFormatter);
        time = currentDateTime.format(timeFormatter);

        // temporary random stats
        happiness = random.nextInt(100) + 1;
        energy = random.nextInt(100) + 1;
        hunger = random.nextInt(100) + 1;
        timesChatted = random.nextInt(20) + 1;
        timesFed = random.nextInt(15) + 1;
        timesTuckedIn = random.nextInt(10) + 1;
        levelProgress = random.nextInt(100) + 1;
        expGained = random.nextInt(50) + 1;
        petLevel = random.nextInt(5) + 1;

        
        petType = "Dragon";
        petName = "Fluffy";

    }

    // Constructor for loading from database
    public JournalEntry(int dbEntryId, String journalText, String date, String time, String username) {
        this.dbEntryId = dbEntryId;
        this.journalText = journalText;
        this.date = date;
        this.time = time;
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getEntryId()
    {
        return entryId;
    }
    public String getJournalText()
    {
        return journalText;
    }
    public void setJournalText(String text)
    {
        journalText = text;
    }

    public String getDate()
    {
        return date;
    }
    public String getTime()
    {
        return time;
    }
    public int getPetLevel()
    {
        return petLevel;
    }
    public String getPetType()
    {
        return petType;
    }
    public String getPetName()
    {
        return petName;
    }
    public int getHappiness()
    {
        return happiness;
    }
    public int getEnergy()
    {
        return energy;
    }
    public int getHunger()
    {
        return hunger;
    }
    public int getTimesChatted()
    {
        return timesChatted;
    }
    public int getTimesFed()
    {
        return timesFed;
    }
    public int getTimesTuckedIn()
    {
        return timesTuckedIn;
    }
    public int getLevelProgress()
    {
        return levelProgress;
    }
    public int getExpGained()
    {
        return expGained;
    }
}

