package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.domain.Dog
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetBreedByIdTest {

    private val repository = mockk<BreedByIdRepository>()
    private val useCase = GetBreedById(repository)

    @Test
    fun `invoke returns breed from repository`() = runTest {
        val expected = Dog(name = "Poodle", icon = "poodle.png")
        coEvery { repository.getBreedById(1) } returns expected

        val result = useCase.invoke(1)

        assertEquals(expected, result)
        coVerify { repository.getBreedById(1) }
    }

    @Test
    fun `invoke returns default dog when repository returns empty`() = runTest {
        val expected = Dog()
        coEvery { repository.getBreedById(999) } returns expected

        val result = useCase.invoke(999)

        assertEquals(expected, result)
        assertEquals("", result.name)
    }
}
