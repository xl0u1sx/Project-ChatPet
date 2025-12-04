package com.example.chatpet

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.chatpet.ui.theme.ChatPetTheme

/**
 * Kotlin helper functions to allow Java tests to use Compose testing APIs
 * These wrap the Kotlin extension functions so they can be called from Java
 */
object ComposeTestHelpers {

    @JvmStatic
    fun createComposeTestRule(): ComposeContentTestRule {
        return createComposeRule()
    }

    @JvmStatic
    fun setMainScreenContent(rule: ComposeContentTestRule, chatViewModel: ChatViewModel) {
        rule.setContent {
            ChatPetTheme {
                MainScreen(chatViewModel = chatViewModel)
            }
        }
    }

    @JvmStatic
    fun onNodeWithText(
        rule: ComposeContentTestRule,
        text: String,
        substring: Boolean = false,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false
    ): SemanticsNodeInteraction {
        return rule.onNodeWithText(text, substring, ignoreCase, useUnmergedTree)
    }

    @JvmStatic
    fun onNodeWithTag(
        rule: ComposeContentTestRule,
        testTag: String,
        useUnmergedTree: Boolean = false
    ): SemanticsNodeInteraction {
        return rule.onNodeWithTag(testTag, useUnmergedTree)
    }

    @JvmStatic
    fun onNode(
        rule: ComposeContentTestRule,
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean = false
    ): SemanticsNodeInteraction {
        return rule.onNode(matcher, useUnmergedTree)
    }

    @JvmStatic
    fun hasText(text: String, substring: Boolean = false, ignoreCase: Boolean = false): SemanticsMatcher {
        return androidx.compose.ui.test.hasText(text, substring, ignoreCase)
    }

    @JvmStatic
    fun performTextInput(interaction: SemanticsNodeInteraction, text: String) {
        interaction.performTextInput(text)
    }

    @JvmStatic
    fun performClick(interaction: SemanticsNodeInteraction) {
        interaction.performClick()
    }

    @JvmStatic
    fun assertExists(interaction: SemanticsNodeInteraction): SemanticsNodeInteraction {
        return interaction.assertExists()
    }

    @JvmStatic
    fun assertDoesNotExist(interaction: SemanticsNodeInteraction): Unit {
        interaction.assertDoesNotExist()
    }
}
