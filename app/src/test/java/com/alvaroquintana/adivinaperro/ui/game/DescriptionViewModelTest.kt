package com.alvaroquintana.adivinaperro.ui.game

import app.cash.turbine.test
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetRandomBreedsWithDescription
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DescriptionViewModelTest {

    private val getRandomBreedsWithDescription = mockk<GetRandomBreedsWithDescription>()
    private val testDispatcher = StandardTestDispatcher()

    private val breeds = listOf(
        Dog(name = "Poodle", temperament = "Intelligent", origin = "France", breedGroup = "Non-Sporting"),
        Dog(name = "Bulldog", temperament = "Friendly", origin = "England", breedGroup = "Non-Sporting"),
        Dog(name = "Labrador", temperament = "Outgoing", origin = "Canada", breedGroup = "Sporting"),
        Dog(name = "Beagle", temperament = "Merry", origin = "England", breedGroup = "Hound")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getRandomBreedsWithDescription.invoke(4) } returns breeds
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = DescriptionViewModel(getRandomBreedsWithDescription)

    @Test
    fun `init loads round with 4 options`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(4, state.options.size)
        assertEquals(false, state.isLoading)
        assertTrue(state.correctName.isNotEmpty())
    }

    @Test
    fun `correct option selection increments score`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val correctName = viewModel.state.value.correctName
        viewModel.onOptionSelected(correctName)

        assertEquals(1, viewModel.state.value.score)
        assertEquals(3, viewModel.state.value.lives)
        assertEquals(DescriptionViewModel.AnswerResult.CORRECT, viewModel.state.value.lastResult)
    }

    @Test
    fun `incorrect option selection decrements lives`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val correctName = viewModel.state.value.correctName
        val wrongName = viewModel.state.value.options.first { it != correctName }
        viewModel.onOptionSelected(wrongName)

        assertEquals(0, viewModel.state.value.score)
        assertEquals(2, viewModel.state.value.lives)
        assertEquals(DescriptionViewModel.AnswerResult.INCORRECT, viewModel.state.value.lastResult)
    }

    @Test
    fun `proceedAfterResult navigates when lives reach zero`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Lose all 3 lives
        repeat(3) {
            val correctName = viewModel.state.value.correctName
            val wrongName = viewModel.state.value.options.first { it != correctName }
            viewModel.onOptionSelected(wrongName)
            if (viewModel.state.value.lives > 0) {
                viewModel.proceedAfterResult()
                testDispatcher.scheduler.advanceUntilIdle()
            }
        }

        viewModel.events.test {
            viewModel.proceedAfterResult()
            val event = awaitItem()
            assertTrue(event is DescriptionViewModel.Event.NavigateToResult)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `description text is generated from breed data`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.descriptionText.isNotEmpty())
    }
}
