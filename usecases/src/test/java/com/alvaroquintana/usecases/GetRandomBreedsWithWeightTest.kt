package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.domain.Dog
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRandomBreedsWithWeightTest {

    private val repository = mockk<BreedByIdRepository>()
    private val useCase = GetRandomBreedsWithWeight(repository)

    @Test
    fun `invoke returns breeds with weight from repository`() = runTest {
        val expected = listOf(
            Dog(name = "Labrador", minWeightKg = 25.0, maxWeightKg = 36.0),
            Dog(name = "Chihuahua", minWeightKg = 1.5, maxWeightKg = 3.0)
        )
        coEvery { repository.getRandomBreedsWithWeight(2) } returns expected

        val result = useCase.invoke(2)

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { repository.getRandomBreedsWithWeight(2) }
    }

    @Test
    fun `invoke returns empty list when no breeds available`() = runTest {
        coEvery { repository.getRandomBreedsWithWeight(4) } returns emptyList()

        val result = useCase.invoke(4)

        assertTrue(result.isEmpty())
    }
}
