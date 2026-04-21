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

class GetRandomBreedsWithCareTest {

    private val repository = mockk<BreedByIdRepository>()
    private val useCase = GetRandomBreedsWithCare(repository)

    @Test
    fun `invoke returns breeds from repository`() = runTest {
        val expected = listOf(Dog(name = "A", nutrition = "Nut"))
        coEvery { repository.getRandomBreedsWithCare(1) } returns expected

        val result = useCase.invoke(1)

        assertEquals(expected, result)
        coVerify { repository.getRandomBreedsWithCare(1) }
    }

    @Test
    fun `invoke returns empty list when none available`() = runTest {
        coEvery { repository.getRandomBreedsWithCare(4) } returns emptyList()

        val result = useCase.invoke(4)

        assertTrue(result.isEmpty())
    }
}
