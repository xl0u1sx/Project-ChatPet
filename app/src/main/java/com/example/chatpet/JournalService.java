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
    static String formatPrompt(JournalEntry entry, String previousJournal) {
        // Truncate previous journal if too long to keep context short
        String truncatedPrevious = previousJournal;
        if (!previousJournal.isEmpty() && previousJournal.length() > 150) {
            truncatedPrevious = previousJournal.substring(0, 150) + "...";
        }
        
        return String.format(
                "You are %s, a level %d %s. Write a SHORT diary entry (2-3 sentences) about your day.\n" +
                        "Stats - Happiness: %d, Energy: %d, Hunger: %d, Chats: %d, Fed: %d, Naps: %d\n" +
                        "Previous: %s\n" +
                        "Be concise and natural.",
                entry.getPetName(), entry.getPetLevel(), entry.getPetType(),
                entry.getHappiness(), entry.getEnergy(), entry.getHunger(),
                entry.getTimesChatted(), entry.getTimesFed(), entry.getTimesTuckedIn(),
                truncatedPrevious.isEmpty() ? "First entry" : truncatedPrevious
        );
    }

}
