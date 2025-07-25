package com.alvaroquintana.adivinaperro.datasource.db

import com.alvaroquintana.domain.Dog

fun DogEntity.toDomain(): Dog = Dog(
    icon = icon,
    name = name,
    origin = origin,
    breedGroup = breedGroup,
    temperament = temperament,
    description = description,
    sizeCategory = sizeCategory,
    minWeightKg = minWeightKg,
    maxWeightKg = maxWeightKg,
    minHeightCm = minHeightCm,
    maxHeightCm = maxHeightCm,
    lifeSpanMin = lifeSpanMin,
    lifeSpanMax = lifeSpanMax,
    coatType = coatType,
    colors = colors,
    exerciseNeeds = exerciseNeeds,
    groomingNeeds = groomingNeeds,
    goodWithChildren = goodWithChildren,
    goodWithOtherDogs = goodWithOtherDogs,
    trainability = trainability,
    barkingLevel = barkingLevel,
    funFact = funFact,
    images = images,
    dataVersion = dataVersion
)

fun Dog.toEntity(id: Int): DogEntity = DogEntity(
    id = id,
    name = name,
    icon = icon,
    origin = origin,
    breedGroup = breedGroup,
    temperament = temperament,
    description = description,
    sizeCategory = sizeCategory,
    minWeightKg = minWeightKg,
    maxWeightKg = maxWeightKg,
    minHeightCm = minHeightCm,
    maxHeightCm = maxHeightCm,
    lifeSpanMin = lifeSpanMin,
    lifeSpanMax = lifeSpanMax,
    coatType = coatType,
    colors = colors,
    exerciseNeeds = exerciseNeeds,
    groomingNeeds = groomingNeeds,
    goodWithChildren = goodWithChildren,
    goodWithOtherDogs = goodWithOtherDogs,
    trainability = trainability,
    barkingLevel = barkingLevel,
    funFact = funFact,
    images = images,
    dataVersion = dataVersion
)
