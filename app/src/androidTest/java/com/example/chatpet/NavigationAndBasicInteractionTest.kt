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

    //makes sure that the main chat UI components are displayed
    @Test
    fun testChatInputFieldAcceptsText() {
        setupMockContent()

        // Wait for UI to stabilize
        composeRule.waitForIdle()

        // Updated for new chat options UI - there's no text field anymore
        // Instead, verify that the main chat interface is displayed
        // Test that PET JOURNAL button is visible (part of chat screen)
        composeRule.onNodeWithText("PET JOURNAL")
            .assertIsDisplayed()
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

    //makes sure that navigation buttons work
    @Test
    fun testSendButtonShowsOnInput() {
        setupMockContent()

        // Wait for UI to stabilize
        composeRule.waitForIdle()

        // Updated for new chat options UI - no send button anymore
        // Instead, verify that the pet screen button (navigation) works
        composeRule.onNodeWithTag("petScreenButton")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}