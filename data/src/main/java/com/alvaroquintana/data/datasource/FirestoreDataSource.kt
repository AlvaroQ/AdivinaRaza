package com.alvaroquintana.data.datasource

import com.alvaroquintana.domain.User

interface FirestoreDataSource {
    suspend fun addRecord(user: User): Result<User>
    suspend fun getRanking(): MutableList<User>
    suspend fun getWorldRecords(limit: Long): String
}