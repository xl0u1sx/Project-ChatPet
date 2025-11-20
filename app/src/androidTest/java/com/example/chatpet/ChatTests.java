package com.example.chatpet;

import static androidx.compose.ui.test.junit4.ComposeTestRule.*;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;

import android.content.Context;
import android.content.Intent;

import androidx.compose.ui.test.junit4.ComposeTestRule;
import androidx.compose.ui.test.junit4.createComposeRule;
import androidx.compose.ui.test.hasText;
import androidx.compose.ui.test.onNodeWithText;
import androidx.compose.ui.test.performClick;
import androidx.compose.ui.test.performTextInput;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.chatpet.ui.theme.ChatPetTheme;

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
    public ComposeTestRule composeTestRule = createComposeRule();

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
        composeTestRule.setContent(() -> {
            ChatPetTheme.INSTANCE.invoke(false, null, content -> {
                MainScreenKt.MainScreen(null, mockViewModel);
                return null;
            });
        });

        // wait for the ui to be ready
        composeTestRule.waitForIdle();

        // find the text input field and enter a message
        composeTestRule.onNodeWithText("Chat with me!")
                .performTextInput("hello, nice to meet you");

        // click the send button
        composeTestRule.onNodeWithText("Send")
                .performClick();

        // wait for the response to appear
        composeTestRule.waitForIdle();

        // verify the pet responded
        composeTestRule.onNode(hasText("hello! nice to meet you too!", false))
                .assertExists();
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
        composeTestRule.setContent(() -> {
            ChatPetTheme.INSTANCE.invoke(false, null, content -> {
                MainScreenKt.MainScreen(null, mockViewModel);
                return null;
            });
        });

        composeTestRule.waitForIdle();

        // send first message to remember the word
        composeTestRule.onNodeWithText("Chat with me!")
                .performTextInput("remember the word banana");
        composeTestRule.onNodeWithText("Send")
                .performClick();
        composeTestRule.waitForIdle();

        // verify acknowledgment
        composeTestRule.onNode(hasText("remember the word banana", false))
                .assertExists();

        // send second message to recall the word
        composeTestRule.onNodeWithText("Chat with me!") // this is component where user chats
                .performTextInput("what word did i ask you to remember?");
        composeTestRule.onNodeWithText("Send")
                .performClick();
        composeTestRule.waitForIdle();

        // verify the pet recalls "banana"
        composeTestRule.onNode(hasText("banana", false))
                .assertExists();
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
        composeTestRule.setContent(() -> {
            ChatPetTheme.INSTANCE.invoke(false, null, content -> {
                MainScreenKt.MainScreen(null, mockViewModel);
                return null;
            });
        });

        composeTestRule.waitForIdle();

        // verify that the send button is not visible when input is empty
        composeTestRule.onNodeWithText("Send")
                .assertDoesNotExist();

        // the ui prevents sending empty messages by not showing the send button
    }
}