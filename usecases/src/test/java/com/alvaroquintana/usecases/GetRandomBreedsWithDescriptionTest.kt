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

class GetRandomBreedsWithDescriptionTest {

    private val repository = mockk<BreedByIdRepository>()
    private val useCase = GetRandomBreedsWithDescription(repository)

    @Test
    fun `invoke returns breeds with description from repository`() = runTest {
        val expected = listOf(
            Dog(name = "Labrador", description = "Friendly and outgoing"),
            Dog(name = "Poodle", description = "Intelligent and elegant")
        )
        coEvery { repository.getRandomBreedsWithDescription(2) } returns expected

        val result = useCase.invoke(2)

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { repository.getRandomBreedsWithDescription(2) }
    }

    @Test
    fun `invoke returns empty list when no breeds available`() = runTest {
        coEvery { repository.getRandomBreedsWithDescription(4) } returns emptyList()

        val result = useCase.invoke(4)

        assertTrue(result.isEmpty())
    }
}
