package com.alvaroquintana.data.repository

import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.App
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppsRecommendedRepositoryTest {

    private val dataBaseSource = mockk<DataBaseSource>()
    private val repository = AppsRecommendedRepository(dataBaseSource)

    @Test
    fun `getAppsRecommended delegates to data source`() = runTest {
        val expected = mutableListOf(
            App(name = "App1", url = "https://play.google.com/app1", image = "app1.png"),
            App(name = "App2", url = "https://play.google.com/app2", image = "app2.png")
        )
        coEvery { dataBaseSource.getAppsRecommended() } returns expected

        val result = repository.getAppsRecommended()

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { dataBaseSource.getAppsRecommended() }
    }

    @Test
    fun `getAppsRecommended returns empty list when no apps`() = runTest {
        coEvery { dataBaseSource.getAppsRecommended() } returns mutableListOf()

        val result = repository.getAppsRecommended()

        assertTrue(result.isEmpty())
    }
}
