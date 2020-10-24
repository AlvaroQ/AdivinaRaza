package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.domain.Dog

class GetBreedById(private val breedByIdRepository: BreedByIdRepository) {

    suspend fun invoke(id: Int): Dog = breedByIdRepository.getBreedById(id)

}
