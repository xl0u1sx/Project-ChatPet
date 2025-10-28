package com.example.chatpet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class JournalActivity extends AppCompatActivity {
    private static final String TAG = "JournalActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // initialize
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        initializeViews();

        setupButtonListeners();
        observeViewModel();

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
            journalViewModel.generateJournal(getApplicationContext(), modelPath);
        });


        // empty for now
        viewHistoryButton.setOnClickListener(v -> {
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
