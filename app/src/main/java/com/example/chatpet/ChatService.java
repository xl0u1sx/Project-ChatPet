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
    private UserRepository userRepository;
    private String currentUsername;

    public ChatService(Context context) {
        /*
        ChatService module that communicates with LLM api using prompt & user msg
        Now with conversation history tracking with database persistence
        */
        this.conversationHistory = new ArrayList<>();
        this.userRepository = new UserRepository(context);
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

    /**
     * Set the current username for chat history tracking
     */
    public void setUsername(String username) {
        this.currentUsername = username;
        // Load chat history when username is set
        loadChatHistory();
    }

    /**
     * Load chat history from database for current user and date
     */
    public void loadChatHistory() {
        if (currentUsername == null || userRepository == null) {
            Log.d("ChatService", "Cannot load chat history: username or repository is null");
            return;
        }

        updateCurrentDate();
        conversationHistory.clear();

        List<UserRepository.ChatMessageData> dbMessages =
                userRepository.getChatMessages(currentUsername, currentDate);

        for (UserRepository.ChatMessageData dbMsg : dbMessages) {
            conversationHistory.add(new ChatMessage(
                    dbMsg.getRole(),
                    dbMsg.getMessage(),
                    dbMsg.getTimestamp()
            ));
        }

        Log.d("ChatService", "Loaded " + conversationHistory.size() + " messages from database");
    }

    private void updateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());

        // If date changed, clear conversation history and reload from database
        if (!today.equals(currentDate)) {
            currentDate = today;
            conversationHistory.clear();
            if (currentUsername != null) {
                loadChatHistory();
            }
            Log.d("ChatService", "New day detected, reloaded conversation history for date: " + currentDate);
        }
    }

    private void addToHistory(String role, String message) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timestamp = timeFormat.format(new Date());

        ChatMessage chatMessage = new ChatMessage(role, message, timestamp);
        conversationHistory.add(chatMessage);

        // Save to database
        if (currentUsername != null && userRepository != null) {
            userRepository.saveChatMessage(currentUsername, role, message, timestamp, currentDate);
        }

        // Optional: Limit history size to prevent memory issues
        if (conversationHistory.size() > 6) {
            conversationHistory.remove(0); // Remove oldest message from memory
            // Note: We keep all messages in database for persistence
        }
    }

    private String buildConversationContext(String basePrompt) {
        StringBuilder context = new StringBuilder();
        context.append(basePrompt);

        // Estimate tokens: roughly 1 token per 4 characters
        // Keep input under 300 tokens to leave room for output (~200 tokens)
        int maxContextTokens = 300;
        int basePromptTokens = estimateTokens(basePrompt);
        int remainingTokens = maxContextTokens - basePromptTokens - 50; // Reserve 50 tokens for current message

        if (!conversationHistory.isEmpty() && remainingTokens > 0) {
            context.append("\n\nRecent conversation:\n");
            
            // Only include recent messages that fit within token limit
            // Start from the most recent messages
            List<ChatMessage> recentMessages = new ArrayList<>();
            int currentTokenCount = 0;
            
            // Work backwards from most recent messages
            for (int i = conversationHistory.size() - 1; i >= 0; i--) {
                ChatMessage msg = conversationHistory.get(i);
                int msgTokens = estimateTokens(msg.message) + 10; // +10 for "User: " or "Assistant: "
                
                if (currentTokenCount + msgTokens <= remainingTokens) {
                    recentMessages.add(0, msg); // Add to beginning to maintain order
                    currentTokenCount += msgTokens;
                } else {
                    break; // Stop if we exceed token limit
                }
                
                // Limit to last 6 messages (3 exchanges) maximum
                if (recentMessages.size() >= 6) {
                    break;
                }
            }
            
            // Add the selected recent messages to context
            for (ChatMessage msg : recentMessages) {
                if ("user".equals(msg.role)) {
                    context.append("User: ").append(msg.message).append("\n");
                } else {
                    context.append("Assistant: ").append(msg.message).append("\n");
                }
            }
            
            Log.d("ChatService", "Included " + recentMessages.size() + " recent messages in context (est. " + currentTokenCount + " tokens)");
        }

        return context.toString();
    }

    /**
     * Estimate token count - rough approximation: 1 token ≈ 4 characters
     */
    private int estimateTokens(String text) {
        if (text == null) return 0;
        return text.length() / 4;
    }

    public String generateResponse(Context context, String modelPath, String userMsg, String prompt) throws Exception {
        /*
        Inputs:
            userMsg, Prompt
            For prompt provide instructions to the llm like 'You are a ... Answer only in ...'

        Output:
            String containing message

        Now includes conversation history with database persistence
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

                // Clear the XNNPack cache to prevent corruption issues
                clearXNNPackCache(context);

                Log.d("ChatService", "Initializing LLM model...");
                LlmInference.LlmInferenceOptions options =
                        LlmInference.LlmInferenceOptions.builder()
                                .setModelPath(modelPath)
                                .setMaxTokens(512)  // Total tokens: input + output
                                .setMaxTopK(40)
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

            // Add both user message and assistant response to history (and database)
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
        if (currentUsername != null && userRepository != null) {
            userRepository.clearChatMessagesForDate(currentUsername, currentDate);
        }
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

    // Clear XNNPack cache to prevent corruption issues
    private void clearXNNPackCache(Context context) {
        try {
            java.io.File cacheDir = context.getCacheDir();
            java.io.File xnnpackCache = new java.io.File(cacheDir, "gemma3-1b-it-int4.task.xnnpack_cache");

            if (xnnpackCache.exists()) {
                boolean deleted = xnnpackCache.delete();
                Log.d("ChatService", "XNNPack cache cleared: " + deleted);
            } else {
                Log.d("ChatService", "XNNPack cache file not found (this is fine for first run)");
            }
        } catch (Exception e) {
            Log.e("ChatService", "Error clearing XNNPack cache: " + e.getMessage(), e);
            // Continue anyway - the cache will be recreated
        }
    }
}