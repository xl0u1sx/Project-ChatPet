package com.example.chatpet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.util.ArrayList;
import java.util.List;

public class JournalService {
    private static final String TAG = "JournalService";
    private final Context context;
    private final UserRepository userRepository;

    public JournalService(Context context) {
        this.context = context;
        this.userRepository = new UserRepository(context);
    }

    public boolean saveJournalEntry(String username, JournalEntry entry) {
        return userRepository.saveJournalEntry(username, entry);
    }

    public List<JournalEntry> getAllJournals(String username) {
        return userRepository.getJournalEntries(username);
    }

    public String getLatestJournalText(String username) {
        JournalEntry latestEntry = userRepository.getLatestJournalEntry(username);
        if (latestEntry != null) {
            return latestEntry.getJournalText();
        }
        return "";
    }

    @SuppressLint("DefaultLocale")
    String formatPrompt(JournalEntry entry, String previousJournal) {
        return String.format(
                "You are a %s named %s at level %d, level progress %d, " +
                        "today's level experience gained %d. Today is %s, time is %s, " +
                        "and this was your previous journal entry %s.\n" +
                        "Write a concise diary entry about your day. Keep it short and to the point.\n" +
                        "Today's Stats:\n" +
                        "- Happiness: %d/100\n" +
                        "- Energy: %d/100\n" +
                        "- Hunger: %d/100\n" +
                        "- Times chatted: %d\n" +
                        "- Times fed: %d\n" +
                        "- Times tucked in: %d\n" +
                        "Write a journal entry from the pet's perspective about the day " +
                        "based on the information above:\n" +
                        "Level 1 = teen\n" +
                        "Level 2 = young adult\n" +
                        "Level 3 = adult.\n" +
                        "Exclude any other statements than your current role as this pet, such as Okay here is your journal.\n" +
                        "Don't start the journal with Okay.",
                entry.getPetType(), entry.getPetName(), entry.getPetLevel(), entry.getLevelProgress(),
                entry.getExpGained(), entry.getDate(), entry.getTime(),
                previousJournal.isEmpty() ? "None" : previousJournal,
                entry.getHappiness(), entry.getEnergy(), entry.getHunger(),
                entry.getTimesChatted(), entry.getTimesFed(), entry.getTimesTuckedIn()
        );
    }

}
