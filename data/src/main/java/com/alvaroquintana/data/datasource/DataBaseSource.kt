package com.alvaroquintana.data.datasource

import com.alvaroquintana.domain.Dog

interface DataBaseSource {
    suspend fun getBreedById(id: Int): Dog
}