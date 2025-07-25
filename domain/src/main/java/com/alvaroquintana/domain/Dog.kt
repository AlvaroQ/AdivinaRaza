package com.alvaroquintana.domain

data class Dog(
    var icon: String = "",
    var name: String = "",
    // Enriched fields (all with defaults for backwards compat with Firebase)
    var origin: String = "",
    var breedGroup: String = "",
    var temperament: String = "",
    var description: String = "",
    var sizeCategory: String = "",  // "Small", "Medium", "Large", "Giant"
    var minWeightKg: Double = 0.0,
    var maxWeightKg: Double = 0.0,
    var minHeightCm: Double = 0.0,
    var maxHeightCm: Double = 0.0,
    var lifeSpanMin: Int = 0,
    var lifeSpanMax: Int = 0,
    var coatType: String = "",
    var colors: String = "",         // Comma-separated
    var exerciseNeeds: Int = 0,      // 1-5 scale
    var groomingNeeds: Int = 0,      // 1-5 scale
    var goodWithChildren: Int = 0,   // 1-5 scale
    var goodWithOtherDogs: Int = 0,  // 1-5 scale
    var trainability: Int = 0,       // 1-5 scale
    var barkingLevel: Int = 0,       // 1-5 scale
    var funFact: String = "",
    var images: String = "",         // Comma-separated URLs
    var dataVersion: Int = 0
)