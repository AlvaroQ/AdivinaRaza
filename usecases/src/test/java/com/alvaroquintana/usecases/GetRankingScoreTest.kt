package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.RankingRepository
import com.alvaroquintana.domain.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRankingScoreTest {

    private val repository = mockk<RankingRepository>()
    private val useCase = GetRankingScore(repository)

    @Test
    fun `invoke returns ranking from repository`() = runTest {
        val expected = mutableListOf(
            User(name = "Player1", points = 100),
            User(name = "Player2", points = 80)
        )
        coEvery { repository.getRanking() } returns expected

        val result = useCase.invoke()

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { repository.getRanking() }
    }

    @Test
    fun `invoke returns empty list when no rankings exist`() = runTest {
        coEvery { repository.getRanking() } returns mutableListOf()

        val result = useCase.invoke()

        assertTrue(result.isEmpty())
    }
}
