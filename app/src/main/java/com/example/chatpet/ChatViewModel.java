// ChatViewModel.java
package com.example.chatpet;

import android.app.Application;
import android.database.Cursor;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.AndroidViewModel;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel";
    
    private final MutableLiveData<LlmUiState> _uiState = new MutableLiveData<>(LlmUiState.Idle.INSTANCE);
    private final LiveData<LlmUiState> uiState = _uiState;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ChatService chatService = new ChatService(); // chat service to connect to LLM api
    public LiveData<LlmUiState> getUiState() {
        return uiState;
    }
    
    public void generateResponse(Context context, String modelPath, String userMsg, String prompt) {
        LlmUiState currentState = _uiState.getValue();
        if (currentState instanceof LlmUiState.Loading) {
            Log.d(TAG, "Already loading, request ignored.");
            return;
        }

        _uiState.postValue(LlmUiState.Loading.INSTANCE);
        Log.i(TAG, "Starting ChatService LLM response generation for user's msg: " + userMsg);

        executorService.execute(() -> {
            try {
                String result = chatService.generateResponse(context, modelPath, userMsg, prompt);
                _uiState.postValue(new LlmUiState.Success(result));
            } catch (Exception e) {
                Log.e(TAG, "Error generating LLM response: " + e.getMessage(), e);
                String errorMessage = e.getMessage() != null ? e.getMessage() : "An unknown error occurred";
                _uiState.postValue(new LlmUiState.Error(errorMessage));
            }
        });


    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Shutdown executor service
        executorService.shutdown();
    }
}

