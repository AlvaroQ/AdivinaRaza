package com.alvaroquintana.adivinaperro.ui.game

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.alvaroquintana.adivinaperro.ui.components.OptionGrid
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun optionGrid_displaysAllOptions() {
        val options = listOf("Poodle", "Bulldog", "Labrador", "Beagle")

        composeTestRule.setContent {
            OptionGrid(options = options) { _, text, modifier ->
                Button(onClick = {}, modifier = modifier) {
                    Text(text)
                }
            }
        }

        composeTestRule.onNodeWithText("Poodle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bulldog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Labrador").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beagle").assertIsDisplayed()
    }

    @Test
    fun optionGrid_clickInvokesCallbackWithCorrectIndex() {
        val options = listOf("Poodle", "Bulldog", "Labrador", "Beagle")
        var clickedIndex = -1

        composeTestRule.setContent {
            OptionGrid(options = options) { index, text, modifier ->
                Button(
                    onClick = { clickedIndex = index },
                    modifier = modifier
                ) {
                    Text(text)
                }
            }
        }

        composeTestRule.onNodeWithText("Labrador").performClick()
        assertEquals(2, clickedIndex)
    }

    @Test
    fun optionGrid_handlesEmptyList() {
        composeTestRule.setContent {
            OptionGrid(options = emptyList()) { _, text, modifier ->
                Button(onClick = {}, modifier = modifier) {
                    Text(text)
                }
            }
        }

        // Should not crash, just display nothing
        composeTestRule.onNodeWithText("Poodle").assertDoesNotExist()
    }
}
