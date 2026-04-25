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

class GetBreedListTest {

    private val repository = mockk<BreedByIdRepository>()
    private val useCase = GetBreedList(repository)

    @Test
    fun `invoke returns breed list from repository`() = runTest {
        val expected = mutableListOf(
            Dog(name = "Poodle", icon = "poodle.png"),
            Dog(name = "Bulldog", icon = "bulldog.png")
        )
        coEvery { repository.getBreedList(0) } returns expected

        val result = useCase.invoke(0)

        assertEquals(expected, result)
        assertEquals(2, result.size)
        coVerify { repository.getBreedList(0) }
    }

    @Test
    fun `invoke returns empty list when no breeds found`() = runTest {
        coEvery { repository.getBreedList(5) } returns mutableListOf()

        val result = useCase.invoke(5)

        assertTrue(result.isEmpty())
    }
}
