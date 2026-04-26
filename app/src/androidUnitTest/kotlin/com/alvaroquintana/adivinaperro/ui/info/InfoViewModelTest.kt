package com.alvaroquintana.adivinaperro.ui.info

import app.cash.turbine.test
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedList
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InfoViewModelTest {

    private val getBreedList = mockk<GetBreedList>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Analytics)
        every { Analytics.analyticsScreenViewed(any()) } returns Unit
        coEvery { getBreedList.invoke(any()) } returns mutableListOf(Dog(name = "Test Dog"))
    }

    @After
    fun tearDown() {
        unmockkObject(Analytics)
        Dispatchers.resetMain()
    }

    private fun createViewModel() = InfoViewModel(getBreedList)

    @Test
    fun `closeDogDetail emits rewarded ad on every fifth viewed item`() = runTest {
        val viewModel = createViewModel()

        viewModel.showingAds.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val initialAdModel = awaitItem()
            assertTrue(initialAdModel is InfoViewModel.UiModel.ShowAd)

            repeat(4) { index ->
                viewModel.selectDog(Dog(name = "Dog-$index"))
                viewModel.closeDogDetail()
            }
            expectNoEvents()

            viewModel.selectDog(Dog(name = "Dog-5"))
            viewModel.closeDogDetail()
            val firstRewarded = awaitItem()
            assertTrue(firstRewarded is InfoViewModel.UiModel.ShowReewardAd)

            repeat(4) { index ->
                viewModel.selectDog(Dog(name = "Dog-${index + 6}"))
                viewModel.closeDogDetail()
            }
            expectNoEvents()

            viewModel.selectDog(Dog(name = "Dog-10"))
            viewModel.closeDogDetail()
            val secondRewarded = awaitItem()
            assertTrue(secondRewarded is InfoViewModel.UiModel.ShowReewardAd)

            cancelAndConsumeRemainingEvents()
        }
    }
}




