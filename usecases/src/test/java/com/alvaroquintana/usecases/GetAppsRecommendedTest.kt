package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.AppsRecommendedRepository
import com.alvaroquintana.domain.App
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAppsRecommendedTest {

    private val repository = mockk<AppsRecommendedRepository>()
    private val useCase = GetAppsRecommended(repository)

    @Test
    fun `invoke returns recommended apps from repository`() = runTest {
        val expected = mutableListOf(
            App(name = "App1", url = "https://play.google.com/app1", image = "app1.png"),
            App(name = "App2", url = "https://play.google.com/app2", image = "app2.png")
        )
        coEvery { repository.getAppsRecommended() } returns expected

        val result = useCase.invoke()

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { repository.getAppsRecommended() }
    }

    @Test
    fun `invoke returns empty list when no apps available`() = runTest {
        coEvery { repository.getAppsRecommended() } returns mutableListOf()

        val result = useCase.invoke()

        assertTrue(result.isEmpty())
    }
}
