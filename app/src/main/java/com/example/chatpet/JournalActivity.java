package com.example.chatpet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    private ProgressBar loadingProgressBar;
    private TextView loadingTextView;
    private TextView statusTextView;
    private TextView journalTitleTextView;
    private TextView journalContentTextView;
    private Button viewHistoryButton;

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
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        statusTextView = findViewById(R.id.statusTextView);
        journalTitleTextView = findViewById(R.id.journalTitleTextView);
        journalContentTextView = findViewById(R.id.journalContentTextView);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);
    }

    private void setupButtonListeners() {
        generateJournalButton.setOnClickListener(v -> {
            Log.d(TAG, "Generate Journal button clicked");
            String modelPath = getString(R.string.model_path);
            journalViewModel.generateJournal(getApplicationContext(), modelPath, username);
        });

        viewHistoryButton.setOnClickListener(v -> {
            Log.d(TAG, "View History button clicked");
            showJournalHistoryDialog();
        });
    }
    
    private void showJournalHistoryDialog() {
        List<JournalEntry> entries = journalViewModel.getJournalHistory().getValue();
        
        if (entries == null || entries.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle("Journal History")
                .setMessage("No journal entries yet. Generate one to get started!")
                .setPositiveButton("OK", null)
                .show();
            return;
        }
        
        // Create a scrollable view with all journal entries
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        for (JournalEntry entry : entries) {
            // Date as header
            TextView dateHeader = new TextView(this);
            dateHeader.setText(entry.getDate() + " - " + entry.getTime());
            dateHeader.setTextSize(16);
            dateHeader.setTextColor(getResources().getColor(android.R.color.holo_purple, null));
            dateHeader.setPadding(0, 20, 0, 10);
            dateHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(dateHeader);
            
            // Journal content
            TextView contentView = new TextView(this);
            contentView.setText(entry.getJournalText());
            contentView.setTextSize(14);
            contentView.setPadding(0, 0, 0, 20);
            layout.addView(contentView);
            
            // Divider
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));
            layout.addView(divider);
        }
        
        scrollView.addView(layout);
        
        new AlertDialog.Builder(this)
            .setTitle("📖 Journal History (" + entries.size() + " entries)")
            .setView(scrollView)
            .setPositiveButton("Close", null)
            .show();
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
        journalTitleTextView.setVisibility(View.GONE);
        journalContentTextView.setVisibility(View.GONE);
    }

    private void handleLoadingState()
    {
        Log.d(TAG, "State: Loading");
        generateJournalButton.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        statusTextView.setVisibility(View.GONE);

        journalTitleTextView.setVisibility(View.GONE);
        journalContentTextView.setVisibility(View.GONE);
    }

    private void handleSuccessState(LlmUiState.Success successState)
    {
        Log.d(TAG, "State: Success");
        generateJournalButton.setEnabled(true);
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        statusTextView.setVisibility(View.GONE);

        journalTitleTextView.setVisibility(View.VISIBLE);
        journalContentTextView.setVisibility(View.VISIBLE);
        journalContentTextView.setText(successState.getResultText());
        viewHistoryButton.setVisibility(View.VISIBLE);
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
        journalTitleTextView.setVisibility(View.GONE);
        journalContentTextView.setVisibility(View.GONE);
    }
}
