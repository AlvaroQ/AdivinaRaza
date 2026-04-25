package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.domain.Dog

class GetRandomBreedsWithCare(private val breedByIdRepository: BreedByIdRepository) {
    suspend fun invoke(count: Int): List<Dog> = breedByIdRepository.getRandomBreedsWithCare(count)
}
