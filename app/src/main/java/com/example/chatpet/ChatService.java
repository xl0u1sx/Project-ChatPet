package com.example.chatpet;
import android.content.Context;
import android.util.Log;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

public class ChatService {

    private LlmInference llm;

    public ChatService() {
        /*
        ChatService module that communicates with LLM api using prompt & user msg

        */

    }

    public String generateResponse(Context context, String modelPath, String userMsg, String prompt) throws Exception {
        /* 
        Inputs:
            userMsg, Prompt
            For prompt provide instructions to the llm like 'You are a ... Answer only in ...'

        Output:
            String containing message
        */
    
        if (userMsg == null || userMsg.trim().isEmpty()) {
            return "Hmmm.. Could you say that again?"; // make sure outside prompt is defined
        }

        // aggregate into prompt for llm
        String llm_prompt = String.format("%s " +
                "\n\n" +
                "User: %s", prompt, userMsg);

        Log.d("ChatService", llm_prompt);

        try {
            LlmInference.LlmInferenceOptions options =
                    LlmInference.LlmInferenceOptions.builder()
                            .setModelPath(modelPath)
                            .setMaxTopK(64)
                            .build();

            llm = LlmInference.createFromOptions(context, options);
            Log.d("ChatService", "Llm created");

            // Generate response — use the aggregated prompt that includes the user's message
            String result = llm.generateResponse(llm_prompt);
            Log.d("ChatService", "Response generated: " + result);
            return result;


        } catch (Exception e) {
            Log.e("ChatService", "Error generating response: " + e.getMessage(), e);
            throw e;
        } finally {
            // Clean up llm
            if (llm != null) {
                try {
                    llm.close();
                } catch (Exception e) {
                    Log.e("ChatService", "Error closing LlmInference: " + e.getMessage(), e);
                } finally {
                    llm = null;
                }

            }

        }



    }



}
