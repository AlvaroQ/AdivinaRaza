package com.alvaroquintana.adivinaperro.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DogEntity::class, SyncMetadata::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}
