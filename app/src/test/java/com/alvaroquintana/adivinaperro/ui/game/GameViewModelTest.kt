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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val getRandomBreedsWithDescription = mockk<GetRandomBreedsWithDescription>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getRandomBreedsWithDescription.invoke(4) } returns listOf(
            Dog(name = "Poodle", icon = "poodle.png"),
            Dog(name = "Bulldog", icon = "bulldog.png"),
            Dog(name = "Labrador", icon = "labrador.png"),
            Dog(name = "Beagle", icon = "beagle.png")
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = GameViewModel(getRandomBreedsWithDescription)

    @Test
    fun `init emits ShowBannerAd true`() = runTest {
        val viewModel = createViewModel()
        viewModel.showingAds.test {
            val item = awaitItem()
            assertTrue(item is GameViewModel.UiModel.ShowBannerAd)
            assertEquals(true, (item as GameViewModel.UiModel.ShowBannerAd).show)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `generateNewStage emits question with icon`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val question = viewModel.question.value
        assertNotEquals("", question)
    }

    @Test
    fun `generateNewStage emits 4 response options`() = runTest {
        coEvery { getRandomBreedsWithDescription.invoke(4) } returns listOf(
            Dog(name = "Poodle", icon = "poodle.png"),
            Dog(name = "Bulldog", icon = "bulldog.png"),
            Dog(name = "Labrador", icon = "labrador.png"),
            Dog(name = "Beagle", icon = "beagle.png")
        )
        val viewModel = createViewModel()

        viewModel.responseOptions.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val options = awaitItem()
            assertEquals(4, options.size)
            assertTrue(options.all { it.isNotEmpty() })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `navigateToResult emits Navigation Result`() = runTest {
        val viewModel = createViewModel()
        viewModel.navigation.test {
            viewModel.navigateToResult("10")
            val nav = awaitItem()
            assertTrue(nav is GameViewModel.Navigation.Result)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `showRewardedAd emits ShowReewardAd`() = runTest {
        val viewModel = createViewModel()
        viewModel.showingAds.test {
            // Skip initial ShowBannerAd
            awaitItem()
            viewModel.showRewardedAd()
            val item = awaitItem()
            assertTrue(item is GameViewModel.UiModel.ShowReewardAd)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getNameBreedCorrect returns correct breed name after stage generation`() = runTest {
        coEvery { getRandomBreedsWithDescription.invoke(4) } returns listOf(
            Dog(name = "Husky", icon = "husky.png"),
            Dog(name = "Poodle", icon = "poodle.png"),
            Dog(name = "Bulldog", icon = "bulldog.png"),
            Dog(name = "Beagle", icon = "beagle.png")
        )
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val correctName = viewModel.getNameBreedCorrect()
        assertTrue(listOf("Husky", "Poodle", "Bulldog", "Beagle").contains(correctName))
    }
}
