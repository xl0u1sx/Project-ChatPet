package com.example.chatpet

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationAndBasicInteractionComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Create a mock pet info
    private val mockPetInfo = UserRepository.PetInfo("Daisy", "Unicorn", 1)

    // Helper function to set up the test environment
    private fun setupMockContent() {
        // uses a manual subclass due to previous runtime crashes
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val userRepository = object : UserRepository(context) {
            override fun getPetInfo(username: String?): PetInfo? {
                return mockPetInfo
            }
        }

        composeRule.setContent {
            val chatViewModel: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(userRepository)
            )

            MainScreen(
                modifier = Modifier,
                chatViewModel = chatViewModel
            )
        }
    }

    //makes sure that the pet screen button is visible and clickable
    @Test
    fun testPetScreenButtonIsVisible() {
        setupMockContent()

        composeRule.onNodeWithTag("petScreenButton")
            .assertIsDisplayed()
            .performClick()
    }

    //makes sure that the text field can take in text
    @Test
    fun testChatInputFieldAcceptsText() {
        setupMockContent()

        val inputText = "hii pet!"

        composeRule.onNodeWithTag("chatTextField")
            .performTextInput(inputText)

        val nodeText = composeRule.onNodeWithTag("chatTextField")
            .fetchSemanticsNode()
            .config[SemanticsProperties.EditableText] as AnnotatedString

        assert(nodeText.text == inputText) {
            "Expected '$inputText', got '${nodeText.text}'"
        }
    }

    //tests visibility and clickability of the pet journal button
    @Test
    fun testPetJournalButtonIsVisibleAndClickable() {
        setupMockContent()

        composeRule.onNodeWithText("PET JOURNAL")
            .assertIsDisplayed()
            .performClick()
    }

    @Test
    fun testNavigationButtonClickSimulation() {
        setupMockContent()

        composeRule.onNodeWithTag("petScreenButton")
            .assertIsDisplayed()
            .performClick()
    }

    //makes sure that the send button shows up when there is input but not when there isn't
    @Test
    fun testSendButtonShowsOnInput() {
        setupMockContent()

        // Initially shouldn't exist if the input is blank
        composeRule.onAllNodesWithText("Send")
            .assertCountEquals(0)

        // Type input
        composeRule.onNodeWithTag("chatTextField")
            .performTextInput("Hi!")

        //send button should appear now
        composeRule.onNodeWithText("Send")
            .assertIsDisplayed()
            .performClick()
    }
}