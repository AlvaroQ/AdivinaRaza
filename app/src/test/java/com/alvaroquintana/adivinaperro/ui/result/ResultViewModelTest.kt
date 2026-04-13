package com.alvaroquintana.adivinaperro.ui.result

import app.cash.turbine.test
import com.alvaroquintana.domain.App
import com.alvaroquintana.usecases.GetAppsRecommended
import com.alvaroquintana.usecases.GetRecordScore
import com.alvaroquintana.usecases.SaveTopScore
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
class ResultViewModelTest {

    private val getAppsRecommended = mockk<GetAppsRecommended>()
    private val saveTopScore = mockk<SaveTopScore>(relaxed = true)
    private val getRecordScore = mockk<GetRecordScore>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getAppsRecommended.invoke() } returns mutableListOf(
            App(name = "TestApp", image = "test.png", url = "com.test.app")
        )
        coEvery { getRecordScore.invoke(1) } returns "100"
        coEvery { getRecordScore.invoke(30) } returns "10"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ResultViewModel(getAppsRecommended, saveTopScore, getRecordScore)

    @Test
    fun `init loads apps recommended`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val apps = viewModel.list.value
        assertEquals(1, apps.size)
        assertEquals("TestApp", apps[0].name)
    }

    @Test
    fun `init loads world record`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("100", viewModel.worldRecord.value)
    }

    @Test
    fun `init emits ShowAd`() = runTest {
        val viewModel = createViewModel()
        viewModel.showingAds.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val item = awaitItem()
            assertTrue(item is ResultViewModel.UiModel.ShowAd)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `navigateToGame emits Navigation Game`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigation.test {
            viewModel.navigateToGame()
            val nav = awaitItem()
            assertTrue(nav is ResultViewModel.Navigation.Game)
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
    fun `navigateToRanking emits Navigation Ranking`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigation.test {
            viewModel.navigateToRanking()
            val nav = awaitItem()
            assertTrue(nav is ResultViewModel.Navigation.Ranking)
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

    @Test
    fun `setPersonalRecordOnServer shows dialog when score is in top 30`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigation.test {
            viewModel.setPersonalRecordOnServer(50) // 50 > 10 (last classified)
            testDispatcher.scheduler.advanceUntilIdle()
            val nav = awaitItem()
            assertTrue(nav is ResultViewModel.Navigation.Dialog)
            cancelAndConsumeRemainingEvents()
        }
    }
}
