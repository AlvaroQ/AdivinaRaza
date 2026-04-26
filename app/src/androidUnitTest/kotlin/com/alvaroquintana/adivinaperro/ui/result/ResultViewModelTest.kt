package com.alvaroquintana.adivinaperro.ui.result

import app.cash.turbine.test
import com.alvaroquintana.adivinaperro.managers.Analytics
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Analytics)
        every { Analytics.analyticsScreenViewed(any()) } returns Unit
        every { Analytics.analyticsClicked(any()) } returns Unit
    }

    @After
    fun tearDown() {
        unmockkObject(Analytics)
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ResultViewModel()

    @Test
    fun `navigateToSelect emits Navigation Select`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigation.test {
            viewModel.navigateToSelect()
            val nav = awaitItem()
            assertTrue(nav is ResultViewModel.Navigation.Select)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `navigateToRate emits Navigation Rate`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigation.test {
            viewModel.navigateToRate()
            val nav = awaitItem()
            assertTrue(nav is ResultViewModel.Navigation.Rate)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `navigateToShare emits Navigation Share with points`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigation.test {
            viewModel.navigateToShare(42)
            val nav = awaitItem()
            assertTrue(nav is ResultViewModel.Navigation.Share)
            assertEquals(42, (nav as ResultViewModel.Navigation.Share).points)
            cancelAndConsumeRemainingEvents()
        }
    }
}
