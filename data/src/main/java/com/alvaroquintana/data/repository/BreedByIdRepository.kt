package com.alvaroquintana.data.repository

import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.Dog

class BreedByIdRepository(private val dataBaseSource: DataBaseSource) {

    suspend fun getBreedById(id: Int): Dog = dataBaseSource.getBreedById(id)

    suspend fun getBreedList(currentPage: Int): MutableList<Dog> = dataBaseSource.getBreedList(currentPage)

    suspend fun getRandomBreedsWithWeight(count: Int): List<Dog> = dataBaseSource.getRandomBreedsWithWeight(count)

    suspend fun getRandomBreedsWithDescription(count: Int): List<Dog> = dataBaseSource.getRandomBreedsWithDescription(count)

}