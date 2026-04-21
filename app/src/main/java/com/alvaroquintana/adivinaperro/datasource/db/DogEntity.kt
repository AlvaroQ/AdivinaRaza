package com.alvaroquintana.adivinaperro.datasource.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dogs")
data class DogEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val icon: String,
    val origin: String = "",
    val breedGroup: String = "",
    val temperament: String = "",
    val description: String = "",
    val sizeCategory: String = "",
    val minWeightKg: Double = 0.0,
    val maxWeightKg: Double = 0.0,
    val minHeightCm: Double = 0.0,
    val maxHeightCm: Double = 0.0,
    val lifeSpanMin: Int = 0,
    val lifeSpanMax: Int = 0,
    val coatType: String = "",
    val colors: String = "",
    val exerciseNeeds: Int = 0,
    val groomingNeeds: Int = 0,
    val goodWithChildren: Int = 0,
    val goodWithOtherDogs: Int = 0,
    val trainability: Int = 0,
    val barkingLevel: Int = 0,
    val funFact: String = "",
    val images: String = "",
    val dataVersion: Int = 0,

    // breedES fields
    val nutrition: String = "",
    val hygiene: String = "",
    val lossHair: String = "",
    val commonDiseases: String = "",
    val otherNames: String = "",
    val fciGroup: Int = 0,
    val fciSection: Int = 0,
    val fciSectionType: String = ""
)
