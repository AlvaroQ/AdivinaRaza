package com.alvaroquintana.adivinaperro.datasource.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DogDao {

    @Query("SELECT * FROM dogs ORDER BY id ASC")
    suspend fun getAll(): List<DogEntity>

    @Query("SELECT * FROM dogs WHERE id = :id")
    suspend fun getById(id: Int): DogEntity?

    @Query("SELECT * FROM dogs ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int): List<DogEntity>

    @Query("SELECT COUNT(*) FROM dogs")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dogs: List<DogEntity>)

    @Query("DELETE FROM dogs")
    suspend fun deleteAll()

    @Query("SELECT * FROM dogs WHERE breedGroup = :group ORDER BY name ASC")
    suspend fun getByBreedGroup(group: String): List<DogEntity>

    @Query("SELECT * FROM dogs WHERE sizeCategory = :size ORDER BY name ASC")
    suspend fun getBySize(size: String): List<DogEntity>

    @Query("SELECT DISTINCT breedGroup FROM dogs WHERE breedGroup != '' ORDER BY breedGroup ASC")
    suspend fun getAllBreedGroups(): List<String>

    @Query("SELECT * FROM dogs WHERE maxWeightKg > 0 ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomBreedsWithWeight(count: Int): List<DogEntity>

    @Query("SELECT * FROM dogs WHERE temperament != '' ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomBreedsWithDescription(count: Int): List<DogEntity>
}
