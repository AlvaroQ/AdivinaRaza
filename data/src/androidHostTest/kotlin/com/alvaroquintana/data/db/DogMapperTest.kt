package com.alvaroquintana.data.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DogMapperTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: AdivinaRazaDatabase
    private lateinit var queries: DogsQueries

    @Before
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AdivinaRazaDatabase.Schema.create(driver)
        database = AdivinaRazaDatabase(driver)
        queries = database.dogsQueries
    }

    @After
    fun tearDown() {
        driver.close()
    }

    private fun insertAndRetrieve(): Dogs {
        queries.insertDog(
            id = 1, name = "Poodle", icon = "poodle.png", origin = "France",
            breedGroup = "Non-Sporting", temperament = "Intelligent, Active",
            description = "Elegant and proud", sizeCategory = "Medium",
            minWeightKg = 20.0, maxWeightKg = 32.0,
            minHeightCm = 38.0, maxHeightCm = 60.0,
            lifeSpanMin = 12, lifeSpanMax = 15,
            coatType = "Curly", colors = "White, Black, Apricot",
            exerciseNeeds = 7, groomingNeeds = 9,
            goodWithChildren = 8, goodWithOtherDogs = 7,
            trainability = 10, barkingLevel = 5,
            funFact = "Hypoallergenic", images = "poodle_gallery.png",
            dataVersion = 3,
            nutrition = "Balanced protein", hygiene = "Professional grooming",
            lossHair = "Minimal", commonDiseases = "Eye problems",
            otherNames = "Caniche", fciGroup = 9,
            fciSection = 2, fciSectionType = "Poodle"
        )
        return queries.getById(1).executeAsOne()
    }

    @Test
    fun toDomain_maps_all_string_fields() {
        val dog = insertAndRetrieve().toDomain()

        assertEquals("poodle.png", dog.icon)
        assertEquals("Poodle", dog.name)
        assertEquals("France", dog.origin)
        assertEquals("Non-Sporting", dog.breedGroup)
        assertEquals("Intelligent, Active", dog.temperament)
        assertEquals("Elegant and proud", dog.description)
        assertEquals("Medium", dog.sizeCategory)
        assertEquals("Curly", dog.coatType)
        assertEquals("White, Black, Apricot", dog.colors)
        assertEquals("Hypoallergenic", dog.funFact)
        assertEquals("poodle_gallery.png", dog.images)
        assertEquals("Balanced protein", dog.nutrition)
        assertEquals("Professional grooming", dog.hygiene)
        assertEquals("Minimal", dog.lossHair)
        assertEquals("Eye problems", dog.commonDiseases)
        assertEquals("Caniche", dog.otherNames)
        assertEquals("Poodle", dog.fciSectionType)
    }

    @Test
    fun toDomain_maps_double_fields() {
        val dog = insertAndRetrieve().toDomain()

        assertEquals(20.0, dog.minWeightKg, 0.0001)
        assertEquals(32.0, dog.maxWeightKg, 0.0001)
        assertEquals(38.0, dog.minHeightCm, 0.0001)
        assertEquals(60.0, dog.maxHeightCm, 0.0001)
    }

    @Test
    fun toDomain_converts_long_to_int_correctly() {
        val dog = insertAndRetrieve().toDomain()

        assertEquals(12, dog.lifeSpanMin)
        assertEquals(15, dog.lifeSpanMax)
        assertEquals(7, dog.exerciseNeeds)
        assertEquals(9, dog.groomingNeeds)
        assertEquals(8, dog.goodWithChildren)
        assertEquals(7, dog.goodWithOtherDogs)
        assertEquals(10, dog.trainability)
        assertEquals(5, dog.barkingLevel)
        assertEquals(3, dog.dataVersion)
        assertEquals(9, dog.fciGroup)
        assertEquals(2, dog.fciSection)
    }

    @Test
    fun toDomain_handles_default_values() {
        queries.insertDog(
            id = 99, name = "", icon = "", origin = "",
            breedGroup = "", temperament = "", description = "",
            sizeCategory = "", minWeightKg = 0.0, maxWeightKg = 0.0,
            minHeightCm = 0.0, maxHeightCm = 0.0,
            lifeSpanMin = 0, lifeSpanMax = 0,
            coatType = "", colors = "",
            exerciseNeeds = 0, groomingNeeds = 0,
            goodWithChildren = 0, goodWithOtherDogs = 0,
            trainability = 0, barkingLevel = 0,
            funFact = "", images = "", dataVersion = 0,
            nutrition = "", hygiene = "", lossHair = "",
            commonDiseases = "", otherNames = "",
            fciGroup = 0, fciSection = 0, fciSectionType = ""
        )

        val dog = queries.getById(99).executeAsOne().toDomain()

        assertEquals("", dog.name)
        assertEquals(0.0, dog.minWeightKg, 0.0001)
        assertEquals(0, dog.lifeSpanMin)
        assertEquals(0, dog.fciGroup)
    }
}
