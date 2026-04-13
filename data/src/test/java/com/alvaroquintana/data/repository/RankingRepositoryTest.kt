package com.alvaroquintana.data.repository

import com.alvaroquintana.data.datasource.FirestoreDataSource
import com.alvaroquintana.domain.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RankingRepositoryTest {

    private val firestoreDataSource = mockk<FirestoreDataSource>()
    private val repository = RankingRepository(firestoreDataSource)

    @Test
    fun `addRecord delegates to firestore data source`() = runTest {
        val user = User(name = "Player1", points = 100)
        coEvery { firestoreDataSource.addRecord(user) } returns Result.success(user)

        val result = repository.addRecord(user)

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { firestoreDataSource.addRecord(user) }
    }

    @Test
    fun `getRanking delegates to firestore data source`() = runTest {
        val expected = mutableListOf(
            User(name = "Player1", points = 100),
            User(name = "Player2", points = 80)
        )
        coEvery { firestoreDataSource.getRanking() } returns expected

        val result = repository.getRanking()

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { firestoreDataSource.getRanking() }
    }

    @Test
    fun `getRanking returns empty list when no rankings`() = runTest {
        coEvery { firestoreDataSource.getRanking() } returns mutableListOf()

        val result = repository.getRanking()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getWorldRecords delegates to firestore data source`() = runTest {
        coEvery { firestoreDataSource.getWorldRecords(10L) } returns "150"

        val result = repository.getWorldRecords(10L)

        assertEquals("150", result)
        coVerify { firestoreDataSource.getWorldRecords(10L) }
    }

    @Test
    fun `getWorldRecords returns empty string when no records`() = runTest {
        coEvery { firestoreDataSource.getWorldRecords(1L) } returns ""

        val result = repository.getWorldRecords(1L)

        assertEquals("", result)
    }
}
