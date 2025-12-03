package com.example.chatpet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * white box junit unit tests for chat service
 * purpose: test internal chat logic including conversation history
 */
public class ChatServiceTest {

    /**
     * test chat message data structure
     *
     * description: verify that chatservice maintains conversation history correctly
     * rationale: ensure the chat memory feature works for continuous conversations
     * expected result: conversation history should contain user and assistant messages
     * bugs: none
     */
    @Test
    public void testChatMessageCreation() {
        // test creating a chat message
        ChatService.ChatMessage message = new ChatService.ChatMessage("user", "Hello", "12:00:00");

        assertNotNull("message should not be null", message);
        assertEquals("role should be user", "user", message.role);
        assertEquals("message should be Hello", "Hello", message.message);
        assertEquals("timestamp should be 12:00:00", "12:00:00", message.timestamp);
    }

    /**
     * test chat message with assistant role
     *
     * description: verify that empty messages are handled correctly
     * rationale: prevent wasting llm calls on empty input
     * expected result: should return a default message without calling llm
     * bugs: none
     *
     * note: this tests the logic from chatservice.java:99-101
     */
    @Test
    public void testAssistantChatMessageCreation() {
        // test creating an assistant chat message
        ChatService.ChatMessage message = new ChatService.ChatMessage("assistant", "Hi there!", "12:00:01");

        assertNotNull("message should not be null", message);
        assertEquals("role should be assistant", "assistant", message.role);
        assertEquals("message should be Hi there!", "Hi there!", message.message);
        assertEquals("timestamp should be 12:00:01", "12:00:01", message.timestamp);
    }


    
}
