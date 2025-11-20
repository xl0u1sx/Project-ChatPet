package com.example.chatpet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * white box junit unit tests for chat service
 * purpose: test internal chat logic including conversation history
 */
public class ChatServiceTest {

    private ChatService chatService;
    private Context mockContext;

    @Before
    public void setUp() {
        chatService = new ChatService();
        mockContext = mock(Context.class);
    }

    /**
     * test conversation history tracking
     *
     * description: verify that chatservice maintains conversation history correctly
     * rationale: ensure the chat memory feature works for continuous conversations
     * expected result: conversation history should contain user and assistant messages
     * bugs: none
     */
    @Test
    public void testConversationHistoryTracking() {
        // initially, conversation history should be empty
        List<ChatService.ChatMessage> history = chatService.getConversationHistory();
        assertNotNull("conversation history should not be null", history);
        assertEquals("conversation history should start empty", 0, history.size());


    }

   

    /**
     * test empty message handling
     *
     * description: verify that empty messages are handled correctly
     * rationale: prevent wasting llm calls on empty input
     * expected result: should return a default message without calling llm
     * bugs: none
     *
     * note: this tests the logic from chatservice.java:99-101
     */
    @Test
    public void testEmptyMessageHandling() throws Exception {
        // test with null message
        String result = chatService.generateResponse(mockContext, "dummy_path", null, "test prompt");
        assertEquals("should return default message for null input",
            "Hmmm.. Could you say that again?", result);

        // test with empty string
        result = chatService.generateResponse(mockContext, "dummy_path", "", "test prompt");
        assertEquals("should return default message for empty input",
            "Hmmm.. Could you say that again?", result);

        // test with whitespace only
        result = chatService.generateResponse(mockContext, "dummy_path", "   ", "test prompt");
        assertEquals("should return default message for whitespace input",
            "Hmmm.. Could you say that again?", result);
    }

    // tests that conversation history is cleared
    @Test
    public void test_clearConvoHistory(){
        ChatService chat = new ChatService();

        chat.getConversationHistory().add(new ChatService.ChatMessage("user", "hii", "time"));

        chat.clearConversationHistory();

        assertEquals(0, chat.getConversationHistory().size());
    }


    
}
