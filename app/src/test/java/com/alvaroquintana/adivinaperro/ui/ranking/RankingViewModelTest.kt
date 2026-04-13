package com.alvaroquintana.adivinaperro.ui.ranking

import com.alvaroquintana.domain.User
import com.alvaroquintana.usecases.GetRankingScore
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RankingViewModelTest {

    private val getRankingScore = mockk<GetRankingScore>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads ranking list from use case`() = runTest {
        val expected = mutableListOf(
            User("Alice", 100),
            User("Bob", 80),
            User("Charlie", 60)
        )
        coEvery { getRankingScore.invoke() } returns expected

        val viewModel = RankingViewModel(getRankingScore)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.rankingList.value.size)
        assertEquals("Alice", viewModel.rankingList.value[0].name)
        assertEquals(100, viewModel.rankingList.value[0].points)
    }

    @Test
    fun `init sets loading false after data loads`() = runTest {
        coEvery { getRankingScore.invoke() } returns mutableListOf()

        val viewModel = RankingViewModel(getRankingScore)
        testDispatcher.scheduler.advanceUntilIdle()

        val progress = viewModel.progress.value
        assertTrue(progress is RankingViewModel.UiModel.Loading)
        assertFalse((progress as RankingViewModel.UiModel.Loading).show)
    }

    @Test
    fun `init handles empty ranking list`() = runTest {
        coEvery { getRankingScore.invoke() } returns mutableListOf()

        val viewModel = RankingViewModel(getRankingScore)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.rankingList.value.isEmpty())
    }
}
