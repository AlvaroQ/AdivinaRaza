package com.alvaroquintana.adivinaperro.ui.select

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SelectScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun selectScreen_displaysStartButton() {
        composeTestRule.setContent {
            SelectScreen(
                onNavigateToGame = {},
                onNavigateToBiggerSmaller = {},
                onNavigateToDescription = {},
                onNavigateToFciTrivia = {},
                onNavigateToLearn = {},
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("Start").assertIsDisplayed()
    }

    @Test
    fun selectScreen_displaysLearnButton() {
        composeTestRule.setContent {
            SelectScreen(
                onNavigateToGame = {},
                onNavigateToBiggerSmaller = {},
                onNavigateToDescription = {},
                onNavigateToFciTrivia = {},
                onNavigateToLearn = {},
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("Learn").assertIsDisplayed()
    }

    @Test
    fun selectScreen_startButtonNavigatesToGame() {
        var navigated = false
        composeTestRule.setContent {
            SelectScreen(
                onNavigateToGame = { navigated = true },
                onNavigateToBiggerSmaller = {},
                onNavigateToDescription = {},
                onNavigateToFciTrivia = {},
                onNavigateToLearn = {},
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("Start").performClick()
        assertTrue(navigated)
    }

    @Test
    fun selectScreen_learnButtonNavigatesToLearn() {
        var navigated = false
        composeTestRule.setContent {
            SelectScreen(
                onNavigateToGame = {},
                onNavigateToBiggerSmaller = {},
                onNavigateToDescription = {},
                onNavigateToFciTrivia = {},
                onNavigateToLearn = { navigated = true },
                onNavigateToSettings = {}
            )
        }

        composeTestRule.onNodeWithText("Learn").performClick()
        assertTrue(navigated)
    }
}
