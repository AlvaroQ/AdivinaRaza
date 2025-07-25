package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.RankingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRecordScoreTest {

    private val repository = mockk<RankingRepository>()
    private val useCase = GetRecordScore(repository)

    @Test
    fun `invoke returns world record from repository`() = runTest {
        coEvery { repository.getWorldRecords(10L) } returns "150"

        val result = useCase.invoke(10L)

        assertEquals("150", result)
        coVerify { repository.getWorldRecords(10L) }
    }

    @Test
    fun `invoke returns empty string when no records exist`() = runTest {
        coEvery { repository.getWorldRecords(1L) } returns ""

        val result = useCase.invoke(1L)

        assertEquals("", result)
    }
}
