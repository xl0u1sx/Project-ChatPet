package com.example.chatpet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.chatpet.ComposeTestHelpers.*;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.compose.ui.test.SemanticsNodeInteraction;
import androidx.compose.ui.test.junit4.ComposeContentTestRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * black box espresso tests for chat functionality using compose ui testing
 * tests verify chat behavior from the user's perspective
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChatTests {

    @Rule
    public ComposeContentTestRule composeTestRule = createComposeTestRule();

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    /**
     * test case 3: valid chat response
     *
     * description: from the chat screen, enter "hello, nice to meet you"
     * rationale: test if llm call responds correctly addressing the user
     * expected result: pet responds correctly addressing the user
     * bugs: none
     *
     * note: this test uses a mock viewmodel to avoid llm calls during testing
     */
    @Test
    public void testValidChatResponse() {
        // create a mock viewmodel with controlled responses
        ChatViewModel mockViewModel = new ChatViewModel() {
            private final MutableLiveData<LlmUiState> testUiState =
                    new MutableLiveData<>(LlmUiState.Idle.INSTANCE);

            @Override
            public androidx.lifecycle.LiveData<LlmUiState> getUiState() {
                return testUiState;
            }

            @Override
            public void generateResponse(Context ctx, String modelPath, String userMsg, String prompt) {
                // simulate a response that addresses the user
                testUiState.postValue(new LlmUiState.Success("hello! nice to meet you too! i'm excited to chat with you!"));
            }
        };

        // set up the compose ui with mock viewmodel
        // src: https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule
        setMainScreenContent(composeTestRule, mockViewModel);

        // wait for the ui to be ready
        composeTestRule.waitForIdle();

        // find the text input field and enter a message
        SemanticsNodeInteraction textField = onNodeWithText(composeTestRule, "Chat with me!", false, false, false);
        performTextInput(textField, "hello, nice to meet you");

        // click the send button
        SemanticsNodeInteraction sendButton = onNodeWithText(composeTestRule, "Send", false, false, false);
        performClick(sendButton);

        // wait for the response to appear
        composeTestRule.waitForIdle();

        // verify the pet responded
        SemanticsNodeInteraction response = onNode(composeTestRule, hasText("hello! nice to meet you too!", true, false), false);
        assertExists(response);
    }

    /**
     * test case 4: chat history
     *
     * description: from the chat screen, tell pet to remember a value "banana",
     * then ask to recall it
     * rationale: test if the pet has memory of the current and past conversations
     * expected result: pet responds with the recalled value
     * bugs: none
     */
    @Test
    public void testChatHistory() {
        // create a mock viewmodel that maintains conversation history
        ChatViewModel mockViewModel = new ChatViewModel() {
            private final MutableLiveData<LlmUiState> testUiState =
                    new MutableLiveData<>(LlmUiState.Idle.INSTANCE);
            private String rememberedWord = null;

            @Override
            public androidx.lifecycle.LiveData<LlmUiState> getUiState() {
                return testUiState;
            }

            @Override
            public void generateResponse(Context ctx, String modelPath, String userMsg, String prompt) {
                // simulate memory of conversation
                if (userMsg.toLowerCase().contains("remember") && userMsg.toLowerCase().contains("banana")) {
                    rememberedWord = "banana";
                    testUiState.postValue(new LlmUiState.Success("okay, i'll remember the word banana!"));
                } else if (userMsg.toLowerCase().contains("what word") || userMsg.toLowerCase().contains("recall")) {
                    if (rememberedWord != null) {
                        testUiState.postValue(new LlmUiState.Success("you asked me to remember: " + rememberedWord));
                    } else {
                        testUiState.postValue(new LlmUiState.Success("i don't remember any word"));
                    }
                } else {
                    testUiState.postValue(new LlmUiState.Success("i'm listening!"));
                }
            }
        };

        // set up the compose ui
        setMainScreenContent(composeTestRule, mockViewModel);

        composeTestRule.waitForIdle();

        // send first message to remember the word
        SemanticsNodeInteraction textField1 = onNodeWithText(composeTestRule, "Chat with me!", false, false, false);
        performTextInput(textField1, "remember the word banana");

        SemanticsNodeInteraction sendButton1 = onNodeWithText(composeTestRule, "Send", false, false, false);
        performClick(sendButton1);
        composeTestRule.waitForIdle();

        // verify acknowledgment
        SemanticsNodeInteraction ack = onNode(composeTestRule, hasText("remember the word banana", true, false), false);
        assertExists(ack);

        // send second message to recall the word
        SemanticsNodeInteraction textField2 = onNodeWithText(composeTestRule, "Chat with me!", false, false, false);
        performTextInput(textField2, "what word did i ask you to remember?");

        SemanticsNodeInteraction sendButton2 = onNodeWithText(composeTestRule, "Send", false, false, false);
        performClick(sendButton2);
        composeTestRule.waitForIdle();

        // verify the pet recalls "banana"
        SemanticsNodeInteraction recall = onNode(composeTestRule, hasText("banana", true, false), false);
        assertExists(recall);
    }

    /**
     * test case 5: empty chat message
     *
     * description: from the chat screen, try to send an empty message
     * rationale: test if empty messages are sent to the pet
     * expected result: send button should not be visible for empty messages.
     * we don't want to waste llm calls on empty messages
     * bugs: none
     */
    @Test
    public void testEmptyChatMessage() {
        // create a simple mock viewmodel
        ChatViewModel mockViewModel = new ChatViewModel() {
            private final MutableLiveData<LlmUiState> testUiState =
                    new MutableLiveData<>(LlmUiState.Idle.INSTANCE);

            @Override
            public androidx.lifecycle.LiveData<LlmUiState> getUiState() {
                return testUiState;
            }
        };

        // set up the compose ui
        setMainScreenContent(composeTestRule, mockViewModel);

        composeTestRule.waitForIdle();

        // verify that the send button is not visible when input is empty
        SemanticsNodeInteraction sendButton = onNodeWithText(composeTestRule, "Send", false, false, false);
        assertDoesNotExist(sendButton);

        // the ui prevents sending empty messages by not showing the send button
    }


}
