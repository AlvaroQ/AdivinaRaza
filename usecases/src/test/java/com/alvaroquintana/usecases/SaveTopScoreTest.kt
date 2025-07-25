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

class SaveTopScoreTest {

    private val repository = mockk<RankingRepository>()
    private val useCase = SaveTopScore(repository)

    @Test
    fun `invoke saves score and returns success`() = runTest {
        val user = User(name = "Player1", points = 100)
        coEvery { repository.addRecord(user) } returns Result.success(user)

        val result = useCase.invoke(user)

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { repository.addRecord(user) }
    }

    @Test
    fun `invoke returns failure when save fails`() = runTest {
        val user = User(name = "Player1", points = 100)
        val exception = RuntimeException("Network error")
        coEvery { repository.addRecord(user) } returns Result.failure(exception)

        val result = useCase.invoke(user)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
