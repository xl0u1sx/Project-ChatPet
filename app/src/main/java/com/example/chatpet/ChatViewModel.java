package com.example.chatpet;

import android.app.Application;
import android.database.Cursor;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel";

    // static instance to persist chat service across mounts
    private static ChatService chatService;

    private final MutableLiveData<LlmUiState> _uiState = new MutableLiveData<>(LlmUiState.Idle.INSTANCE);
    private final LiveData<LlmUiState> uiState = _uiState;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private UserRepository userRepository = null;

    public ChatViewModel() {
        // ChatService will be initialized when setUsername is called
    }

    //for testing
    public ChatViewModel(UserRepository userRepository) {
        this(); // call default constructor
        this.userRepository = userRepository;
    }

    public void initializeChatService(Context context, String username) {
        if (chatService == null) {
            chatService = new ChatService(context);
            Log.d(TAG, "ChatService initialized");
        }
        chatService.setUsername(username);
        Log.d(TAG, "ChatService username set to: " + username);
    }

    public LiveData<LlmUiState> getUiState() {
        return uiState;
    }

    // method to get conversation history for debugging or UI purposes
    public java.util.List<ChatService.ChatMessage> getConversationHistory() {
        return chatService != null ? chatService.getConversationHistory() : new java.util.ArrayList<>();
    }

    // method to clear conversation history manually if needed
    public void clearConversationHistory() {
        if (chatService != null) {
            chatService.clearConversationHistory();
            Log.d(TAG, "Conversation history cleared from ViewModel");
        }
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
        // Clean up chat service resources
        if (chatService != null) {
            chatService.cleanup();
            Log.d(TAG, "ChatService cleaned up");
        }
    }
}