package com.alvaroquintana.data.repository

import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.Dog

class BreedByIdRepository(private val dataBaseSource: DataBaseSource) {

    suspend fun getBreedById(id: Int): Dog = dataBaseSource.getBreedById(id)

    suspend fun getBreedList(currentPage: Int): MutableList<Dog> = dataBaseSource.getBreedList(currentPage)

}