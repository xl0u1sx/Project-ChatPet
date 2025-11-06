package com.example.chatpet;
import android.content.Context;
import android.util.Log;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatService {

    private LlmInference llm;
    private List<ChatMessage> conversationHistory;
    private String currentDate;
    private boolean isLlmInitialized = false;
    private String initializedModelPath = null;

    public ChatService() {
        /*
        ChatService module that communicates with LLM api using prompt & user msg
        Now with conversation history tracking for current day
        */
        this.conversationHistory = new ArrayList<>();
        updateCurrentDate();
    }

    // Inner class to represent a chat message
    public static class ChatMessage {
        public final String role; // "user" or "assistant"
        public final String message;
        public final String timestamp;

        public ChatMessage(String role, String message, String timestamp) {
            this.role = role;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    private void updateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());
        
        // If date changed, clear conversation history
        if (!today.equals(currentDate)) {
            currentDate = today;
            conversationHistory.clear();
            Log.d("ChatService", "New day detected, cleared conversation history");
        }
    }

    private void addToHistory(String role, String message) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timestamp = timeFormat.format(new Date());
        conversationHistory.add(new ChatMessage(role, message, timestamp));
        
        // Optional: Limit history size to prevent memory issues
        if (conversationHistory.size() > 10) {
            conversationHistory.remove(0); // Remove oldest message
        }
    }

    private String buildConversationContext(String basePrompt) {
        StringBuilder context = new StringBuilder();
        context.append(basePrompt);
        
        if (!conversationHistory.isEmpty()) {
            context.append("\n\nPrevious conversation today:\n");
            for (ChatMessage msg : conversationHistory) {
                if ("user".equals(msg.role)) {
                    context.append("User: ").append(msg.message).append("\n");
                } else {
                    context.append("Assistant: ").append(msg.message).append("\n");
                }
            }
        }
        
        return context.toString();
    }

    public String generateResponse(Context context, String modelPath, String userMsg, String prompt) throws Exception {
        /* 
        Inputs:
            userMsg, Prompt
            For prompt provide instructions to the llm like 'You are a ... Answer only in ...'

        Output:
            String containing message
            
        Now includes conversation history for context continuity
        */
        
        // Check if it's a new day and update accordingly
        updateCurrentDate();
    
        if (userMsg == null || userMsg.trim().isEmpty()) {
            return "Hmmm.. Could you say that again?"; // make sure outside prompt is defined
        }

        // Build context with conversation history
        String contextualPrompt = buildConversationContext(prompt);
        
        // aggregate into prompt for llm with current user message
        String llm_prompt = String.format("%s " +
                "\n\n" +
                "User: %s", contextualPrompt, userMsg);

        Log.d("ChatService", "Full prompt with history: " + llm_prompt);

        try {
            // Initialize LLM only once to avoid cache corruption
            if (!isLlmInitialized || !modelPath.equals(initializedModelPath)) {
                // Close existing instance if model path changed
                if (llm != null) {
                    try {
                        llm.close();
                        Log.d("ChatService", "Closed previous LLM instance");
                    } catch (Exception e) {
                        Log.e("ChatService", "Error closing previous LLM: " + e.getMessage(), e);
                    }
                }
                
                Log.d("ChatService", "Initializing LLM model...");
                LlmInference.LlmInferenceOptions options =
                        LlmInference.LlmInferenceOptions.builder()
                                .setModelPath(modelPath)
                                .setMaxTopK(64)
                                .build();

                llm = LlmInference.createFromOptions(context, options);
                isLlmInitialized = true;
                initializedModelPath = modelPath;
                Log.d("ChatService", "LLM initialized successfully");
            } else {
                Log.d("ChatService", "Reusing existing LLM instance");
            }

            // Generate response — use the aggregated prompt that includes conversation history
            String result = llm.generateResponse(llm_prompt);
            Log.d("ChatService", "Response generated: " + result);
            
            // Add both user message and assistant response to history
            addToHistory("user", userMsg);
            addToHistory("assistant", result);
            
            Log.d("ChatService", "Conversation history size: " + conversationHistory.size());
            
            return result;

        } catch (Exception e) {
            Log.e("ChatService", "Error generating response: " + e.getMessage(), e);
            // Don't close LLM on error, keep it for reuse
            throw e;
        }
    }
    
    // Method to get current conversation history (for debugging or UI purposes)
    public List<ChatMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    // Method to clear conversation history manually if needed
    public void clearConversationHistory() {
        conversationHistory.clear();
        Log.d("ChatService", "Conversation history manually cleared");
    }
    
    // Method to clean up resources when service is no longer needed
    public void cleanup() {
        if (llm != null) {
            try {
                llm.close();
                Log.d("ChatService", "LLM instance closed during cleanup");
            } catch (Exception e) {
                Log.e("ChatService", "Error closing LLM during cleanup: " + e.getMessage(), e);
            } finally {
                llm = null;
                isLlmInitialized = false;
                initializedModelPath = null;
            }
        }
    }



}
