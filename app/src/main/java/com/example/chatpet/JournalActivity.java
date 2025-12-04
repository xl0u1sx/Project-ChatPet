package com.example.chatpet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class JournalActivity extends AppCompatActivity {
    private static final String TAG = "JournalActivity";
    private static final String PREFS_NAME = "ChatPetPrefs";
    private static final String KEY_USERNAME = "username";

    // UI Components
    private Button generateJournalButton;
    private Button backButton;
    private ProgressBar loadingProgressBar;
    private TextView loadingTextView;
    private TextView statusTextView;
    private TextView emptyHistoryTextView;
    private LinearLayout journalEntriesContainer;

    // ViewModel
    private JournalViewModel journalViewModel;
    
    // Username for current user
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Get username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        username = prefs.getString(KEY_USERNAME, "user123");
        Log.d(TAG, "JournalActivity created for user: " + username);

        // initialize
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        initializeViews();

        setupButtonListeners();
        observeViewModel();
        
        // Load journal history
        journalViewModel.loadJournalHistory(getApplicationContext(), username);

        Log.d(TAG, "JournalActivity created");
    }

    private void initializeViews() {
        generateJournalButton = findViewById(R.id.generateJournalButton);
        backButton = findViewById(R.id.backButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        statusTextView = findViewById(R.id.statusTextView);
        emptyHistoryTextView = findViewById(R.id.emptyHistoryTextView);
        journalEntriesContainer = findViewById(R.id.journalEntriesContainer);
    }

    private void setupButtonListeners() {
        generateJournalButton.setOnClickListener(v -> {
            Log.d(TAG, "Generate Journal button clicked");
            String modelPath = getString(R.string.model_path);
            journalViewModel.generateJournal(getApplicationContext(), modelPath, username);
        });
        
        backButton.setOnClickListener(v -> {
            finish(); // Close activity and return to previous screen
        });
    }

    private void observeViewModel()
    {
        journalViewModel.getUiState().observe(this, uiState ->
        {
            if (uiState instanceof LlmUiState.Idle)
            {
                handleIdleState();
            }
            else if (uiState instanceof LlmUiState.Loading)
            {
                handleLoadingState();
            }
            else if (uiState instanceof LlmUiState.Success)
            {
                handleSuccessState((LlmUiState.Success) uiState);
            }
            else if (uiState instanceof LlmUiState.Error)
            {
                handleErrorState((LlmUiState.Error) uiState);
            }
        });
        
        // Observe journal history changes
        journalViewModel.getJournalHistory().observe(this, entries -> {
            displayJournalHistory(entries);
        });
    }

    private void handleIdleState()
    {
        Log.d(TAG, "State: Idle");
        generateJournalButton.setEnabled(true);
        loadingProgressBar.setVisibility(View.GONE);

        loadingTextView.setVisibility(View.GONE);
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText("Click button to generate journal entry");
        statusTextView.setTextColor(getResources().getColor(android.R.color.black, null));
    }

    private void handleLoadingState()
    {
        Log.d(TAG, "State: Loading");
        generateJournalButton.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        statusTextView.setVisibility(View.GONE);
    }

    private void handleSuccessState(LlmUiState.Success successState)
    {
        Log.d(TAG, "State: Success");
        generateJournalButton.setEnabled(true);
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText("Journal entry generated successfully! See below.");
        statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
    }

    private void handleErrorState(LlmUiState.Error errorState)
    {
        Log.e(TAG, "State: Error - " + errorState.getErrorMessage());

        generateJournalButton.setEnabled(true);
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);

        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText("Error: " + errorState.getErrorMessage());
        statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
    }
    
    private void displayJournalHistory(List<JournalEntry> entries) {
        Log.d(TAG, "Displaying " + (entries != null ? entries.size() : 0) + " journal entries");
        
        // Clear existing entries
        journalEntriesContainer.removeAllViews();
        
        if (entries == null || entries.isEmpty()) {
            // Show empty message
            emptyHistoryTextView.setVisibility(View.VISIBLE);
            return;
        }
        
        // Hide empty message
        emptyHistoryTextView.setVisibility(View.GONE);
        
        // Add each journal entry as a card
        for (int i = entries.size() - 1; i >= 0; i--) { // Reverse order (newest first)
            JournalEntry entry = entries.get(i);
            
            // Create a card for each entry with rounded corners and shadow
            LinearLayout entryCard = new LinearLayout(this);
            entryCard.setOrientation(LinearLayout.VERTICAL);
            entryCard.setPadding(32, 24, 32, 24);
            entryCard.setBackgroundColor(0xFFFFFFFF); // White background
            entryCard.setElevation(8); // Add shadow
            
            android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
            shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            shape.setCornerRadius(16);
            shape.setColor(0xFFFFFFFF);
            shape.setStroke(2, 0xFFFFB6C1); // Pink border
            entryCard.setBackground(shape);
            
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 24);
            entryCard.setLayoutParams(cardParams);
            
            // Header container with date and expand/collapse button
            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            
            // Date header
            TextView dateHeader = new TextView(this);
            dateHeader.setText(entry.getDate() + " at " + entry.getTime());
            dateHeader.setTextSize(14);
            dateHeader.setTextColor(getResources().getColor(android.R.color.holo_purple, null));
            dateHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            );
            dateHeader.setLayoutParams(dateParams);
            headerLayout.addView(dateHeader);
            
            // Expand/Collapse indicator
            TextView expandIndicator = new TextView(this);
            expandIndicator.setText("▼");
            expandIndicator.setTextSize(16);
            expandIndicator.setTextColor(getResources().getColor(android.R.color.holo_purple, null));
            expandIndicator.setPadding(16, 0, 0, 0);
            headerLayout.addView(expandIndicator);
            
            entryCard.addView(headerLayout);
            
            // Add spacing after header
            android.widget.Space spacer = new android.widget.Space(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16
            ));
            entryCard.addView(spacer);
            
            // Journal content (initially visible)
            TextView contentView = new TextView(this);
            contentView.setText(entry.getJournalText());
            contentView.setTextSize(15);
            contentView.setLineSpacing(6, 1.0f);
            contentView.setTextColor(getResources().getColor(android.R.color.black, null));
            entryCard.addView(contentView);
            
            // Make the entire card clickable to toggle expand/collapse
            entryCard.setClickable(true);
            entryCard.setFocusable(true);
            entryCard.setOnClickListener(v -> {
                if (contentView.getVisibility() == View.VISIBLE) {
                    // Collapse
                    contentView.setVisibility(View.GONE);
                    expandIndicator.setText("▶");
                } else {
                    // Expand
                    contentView.setVisibility(View.VISIBLE);
                    expandIndicator.setText("▼");
                }
            });
            
            // Add the card to the container
            journalEntriesContainer.addView(entryCard);
        }
        
        Log.d(TAG, "Journal history display updated");
    }
}
