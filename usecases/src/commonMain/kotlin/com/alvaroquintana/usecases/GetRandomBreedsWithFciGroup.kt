package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.domain.Dog

class GetRandomBreedsWithFciGroup(private val breedByIdRepository: BreedByIdRepository) {
    suspend fun invoke(count: Int): List<Dog> = breedByIdRepository.getRandomBreedsWithFciGroup(count)
}
