package com.alvaroquintana.adivinaperro.ui.game

import app.cash.turbine.test
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetRandomBreedsWithFciGroup
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
class FciTriviaViewModelTest {

    private val getRandomBreedsWithFciGroup = mockk<GetRandomBreedsWithFciGroup>()
    private val testDispatcher = StandardTestDispatcher()

    private val pool = (1..20).map {
        Dog(
            name = "Dog$it",
            icon = "icon$it.png",
            fciGroup = (it % 6) + 1
        )
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getRandomBreedsWithFciGroup.invoke(any()) } returns pool
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = FciTriviaViewModel(getRandomBreedsWithFciGroup)

    @Test
    fun `init loads options and correct answer`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(4, state.options.size)
        assertTrue(state.correctAnswer.isNotBlank())
        assertTrue(state.questionImage.isNotBlank())
        assertTrue(state.options.contains(state.correctAnswer))
    }

    @Test
    fun `correct selection increments score`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val correct = viewModel.state.value.correctAnswer
        viewModel.onOptionSelected(correct)

        val state = viewModel.state.value
        assertEquals(1, state.score)
        assertEquals(3, state.lives)
        assertEquals(FciTriviaViewModel.AnswerResult.CORRECT, state.lastResult)
    }

    @Test
    fun `incorrect selection decrements lives`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val correct = viewModel.state.value.correctAnswer
        val wrong = viewModel.state.value.options.first { it != correct }
        viewModel.onOptionSelected(wrong)

        val state = viewModel.state.value
        assertEquals(0, state.score)
        assertEquals(2, state.lives)
        assertEquals(FciTriviaViewModel.AnswerResult.INCORRECT, state.lastResult)
    }

    @Test
    fun `proceedAfterResult navigates when lives reach zero`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // lose all lives
        repeat(3) {
            val correct = viewModel.state.value.correctAnswer
            val wrong = viewModel.state.value.options.first { it != correct }
            viewModel.onOptionSelected(wrong)
            if (viewModel.state.value.lives > 0) {
                viewModel.proceedAfterResult()
                testDispatcher.scheduler.advanceUntilIdle()
            }
        }

        viewModel.events.test {
            viewModel.proceedAfterResult()
            val event = awaitItem()
            assertTrue(event is FciTriviaViewModel.Event.NavigateToResult)
            cancelAndConsumeRemainingEvents()
        }
    }
}
