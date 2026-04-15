package com.alvaroquintana.data.repository

import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.Dog
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreedByIdRepositoryTest {

    private val dataBaseSource = mockk<DataBaseSource>()
    private val repository = BreedByIdRepository(dataBaseSource)

    @Test
    fun `getBreedById delegates to data source`() = runTest {
        val expected = Dog(name = "Poodle", icon = "poodle.png")
        coEvery { dataBaseSource.getBreedById(1) } returns expected

        val result = repository.getBreedById(1)

        assertEquals(expected, result)
        coVerify { dataBaseSource.getBreedById(1) }
    }

    @Test
    fun `getBreedList delegates to data source`() = runTest {
        val expected = mutableListOf(
            Dog(name = "Poodle"),
            Dog(name = "Bulldog")
        )
        coEvery { dataBaseSource.getBreedList(0) } returns expected

        val result = repository.getBreedList(0)

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { dataBaseSource.getBreedList(0) }
    }

    @Test
    fun `getBreedList returns empty list when no data`() = runTest {
        coEvery { dataBaseSource.getBreedList(5) } returns mutableListOf()

        val result = repository.getBreedList(5)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getRandomBreedsWithWeight delegates to data source`() = runTest {
        val expected = listOf(
            Dog(name = "Labrador", minWeightKg = 25.0, maxWeightKg = 36.0)
        )
        coEvery { dataBaseSource.getRandomBreedsWithWeight(1) } returns expected

        val result = repository.getRandomBreedsWithWeight(1)

        assertEquals(expected, result)
        coVerify { dataBaseSource.getRandomBreedsWithWeight(1) }
    }

    @Test
    fun `getRandomBreedsWithDescription delegates to data source`() = runTest {
        val expected = listOf(
            Dog(name = "Poodle", description = "Intelligent and elegant")
        )
        coEvery { dataBaseSource.getRandomBreedsWithDescription(1) } returns expected

        val result = repository.getRandomBreedsWithDescription(1)

        assertEquals(expected, result)
        coVerify { dataBaseSource.getRandomBreedsWithDescription(1) }
    }

    @Test
    fun `getRandomBreedsWithFciGroup delegates to data source`() = runTest {
        val expected = listOf(
            Dog(name = "Airedale", fciGroup = 3)
        )
        coEvery { dataBaseSource.getRandomBreedsWithFciGroup(1) } returns expected

        val result = repository.getRandomBreedsWithFciGroup(1)

        assertEquals(expected, result)
        coVerify { dataBaseSource.getRandomBreedsWithFciGroup(1) }
    }

    @Test
    fun `getRandomBreedsWithCare delegates to data source`() = runTest {
        val expected = listOf(
            Dog(name = "Poodle", nutrition = "Nut")
        )
        coEvery { dataBaseSource.getRandomBreedsWithCare(1) } returns expected

        val result = repository.getRandomBreedsWithCare(1)

        assertEquals(expected, result)
        coVerify { dataBaseSource.getRandomBreedsWithCare(1) }
    }
}
