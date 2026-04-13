package com.alvaroquintana.adivinaperro.ui.game

import app.cash.turbine.test
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetRandomBreedsWithWeight
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
class BiggerSmallerViewModelTest {

    private val getRandomBreedsWithWeight = mockk<GetRandomBreedsWithWeight>()
    private val testDispatcher = StandardTestDispatcher()

    private val heavyDog = Dog(name = "Mastiff", maxWeightKg = 90.0, maxHeightCm = 76.0)
    private val lightDog = Dog(name = "Chihuahua", maxWeightKg = 3.0, maxHeightCm = 23.0)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getRandomBreedsWithWeight.invoke(2) } returns listOf(heavyDog, lightDog)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): BiggerSmallerViewModel {
        return BiggerSmallerViewModel(getRandomBreedsWithWeight)
    }

    @Test
    fun `init loads first round with two breeds`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Mastiff", state.breedLeft?.name)
        assertEquals("Chihuahua", state.breedRight?.name)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `correct answer increments score and preserves lives`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onBreedSelected(isLeftSelected = true) // Mastiff is heavier

        val state = viewModel.state.value
        assertEquals(1, state.score)
        assertEquals(3, state.lives)
        assertEquals(BiggerSmallerViewModel.AnswerResult.CORRECT, state.lastResult)
    }

    @Test
    fun `incorrect answer decrements lives and keeps score`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onBreedSelected(isLeftSelected = false) // Chihuahua is lighter, wrong

        val state = viewModel.state.value
        assertEquals(0, state.score)
        assertEquals(2, state.lives)
        assertEquals(BiggerSmallerViewModel.AnswerResult.INCORRECT, state.lastResult)
    }

    @Test
    fun `proceedAfterResult navigates when lives reach zero`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Lose all 3 lives
        repeat(3) {
            coEvery { getRandomBreedsWithWeight.invoke(2) } returns listOf(heavyDog, lightDog)
            viewModel.onBreedSelected(isLeftSelected = false)
            if (viewModel.state.value.lives > 0) {
                viewModel.proceedAfterResult()
                testDispatcher.scheduler.advanceUntilIdle()
            }
        }

        viewModel.events.test {
            viewModel.proceedAfterResult()
            val event = awaitItem()
            assertTrue(event is BiggerSmallerViewModel.Event.NavigateToResult)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `proceedAfterResult shows rewarded ad at stage multiple of 6`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Advance to stage 6 (5 correct answers → stage goes from 1 to 6)
        repeat(5) {
            coEvery { getRandomBreedsWithWeight.invoke(2) } returns listOf(heavyDog, lightDog)
            viewModel.onBreedSelected(isLeftSelected = true)
            viewModel.proceedAfterResult()
            testDispatcher.scheduler.advanceUntilIdle()
        }

        assertEquals(6, viewModel.state.value.stage)

        viewModel.events.test {
            viewModel.onBreedSelected(isLeftSelected = true)
            viewModel.proceedAfterResult()
            val event = awaitItem()
            assertTrue(event is BiggerSmallerViewModel.Event.ShowRewardedAd)
            cancelAndConsumeRemainingEvents()
        }
    }
}
