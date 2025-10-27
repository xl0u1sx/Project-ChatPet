// ChatViewModel.java
package com.example.chatpet;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel";
    
    private final MutableLiveData<LlmUiState> _uiState = new MutableLiveData<>(LlmUiState.Idle.INSTANCE);
    private final LiveData<LlmUiState> uiState = _uiState;
    
    // Keep LlmInference instance if you plan to make multiple calls
    // Ensure it's closed properly in onCleared()
    private LlmInference llmInference = null;
    
    // ExecutorService for background operations (replacement for coroutines)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public LiveData<LlmUiState> getUiState() {
        return uiState;
    }
    
    public void generateResponse(Context context, String modelPath, String prompt) {
        LlmUiState currentState = _uiState.getValue();
        if (currentState instanceof LlmUiState.Loading) {
            Log.d(TAG, "Already loading, request ignored.");
            return;
        }
        
        _uiState.postValue(LlmUiState.Loading.INSTANCE);
        Log.i(TAG, "Starting LLM response generation for prompt: " + prompt);
        
        executorService.execute(() -> {
            try {
                // Initialize LLM Inference if not already done or if it needs to be fresh
                // For simplicity, creating it each time. For performance, you might cache it.
                // Ensure this part is also efficient or handled if llmInference can be reused.
                LlmInference.LlmInferenceOptions taskOptions = 
                    LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(modelPath)
                        .setMaxTopK(64) // Add other options as needed (maxTokens, temperature, etc.)
                        .build();
                
                // createFromOptions can also be blocking
                llmInference = LlmInference.createFromOptions(context, taskOptions);
                Log.d(TAG, "LlmInference instance created/reused.");
                
                // Actual blocking call
                String result = llmInference.generateResponse(prompt);
                
                if (result != null) {
                    Log.i(TAG, "LLM Result: " + result);
                    _uiState.postValue(new LlmUiState.Success(result));
                } else {
                    Log.e(TAG, "LLM result was null");
                    _uiState.postValue(new LlmUiState.Error("LLM returned no result."));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating LLM response: " + e.getMessage(), e);
                String errorMessage = e.getMessage() != null ? e.getMessage() : "An unknown error occurred";
                _uiState.postValue(new LlmUiState.Error(errorMessage));
            } finally {
                // Close the instance if you create it fresh each time,
                // or manage its lifecycle if you reuse it.
                try {
                    if (llmInference != null) {
                        llmInference.close();
                        llmInference = null; // Nullify if creating fresh next time
                    }
                    Log.d(TAG, "LlmInference instance closed.");
                } catch (Exception e) {
                    Log.e(TAG, "Error closing LlmInference: " + e.getMessage(), e);
                }
                // Optionally, you might want to revert to Idle state after an error
                // or if the user should be able to make another request.
            }
        });
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        
        // Clean up resources
        executorService.execute(() -> {
            try {
                if (llmInference != null) {
                    llmInference.close(); // Ensure it's closed when ViewModel is destroyed
                    llmInference = null;
                    Log.d(TAG, "LlmInference instance closed in onCleared.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error closing LlmInference in onCleared: " + e.getMessage(), e);
            }
        });
        
        // Shutdown executor service
        executorService.shutdown();
    }
}

