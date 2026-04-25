package com.alvaroquintana.data.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DogsQueriesTest {

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

    private fun insertSampleDog(
        id: Long = 1,
        name: String = "Labrador",
        breedGroup: String = "Retrievers",
        temperament: String = "Friendly",
        description: String = "Great family dog",
        sizeCategory: String = "Large",
        minWeightKg: Double = 25.0,
        maxWeightKg: Double = 36.0,
        minHeightCm: Double = 54.0,
        maxHeightCm: Double = 62.0,
        fciGroup: Long = 8,
        nutrition: String = "",
        hygiene: String = "",
        lossHair: String = ""
    ) {
        queries.insertDog(
            id = id, name = name, icon = "lab.png", origin = "Canada",
            breedGroup = breedGroup, temperament = temperament,
            description = description, sizeCategory = sizeCategory,
            minWeightKg = minWeightKg, maxWeightKg = maxWeightKg,
            minHeightCm = minHeightCm, maxHeightCm = maxHeightCm,
            lifeSpanMin = 10, lifeSpanMax = 14,
            coatType = "Short", colors = "Yellow, Black, Chocolate",
            exerciseNeeds = 8, groomingNeeds = 4,
            goodWithChildren = 9, goodWithOtherDogs = 8,
            trainability = 9, barkingLevel = 3,
            funFact = "Most popular breed", images = "lab.png",
            dataVersion = 1,
            nutrition = nutrition, hygiene = hygiene,
            lossHair = lossHair, commonDiseases = "Hip dysplasia",
            otherNames = "", fciGroup = fciGroup,
            fciSection = 1, fciSectionType = "Retrievers"
        )
    }

    @Test
    fun insertDog_and_getById_round_trip() {
        insertSampleDog(id = 42, name = "Poodle")

        val result = queries.getById(42).executeAsOneOrNull()

        assertNotNull(result)
        assertEquals("Poodle", result!!.name)
        assertEquals(42L, result.id)
    }

    @Test
    fun getById_returns_null_for_missing_id() {
        val result = queries.getById(999).executeAsOneOrNull()

        assertNull(result)
    }

    @Test
    fun getAll_returns_ordered_by_id() {
        insertSampleDog(id = 3, name = "Chihuahua")
        insertSampleDog(id = 1, name = "Akita")
        insertSampleDog(id = 2, name = "Beagle")

        val result = queries.getAll().executeAsList()

        assertEquals(3, result.size)
        assertEquals("Akita", result[0].name)
        assertEquals("Beagle", result[1].name)
        assertEquals("Chihuahua", result[2].name)
    }

    @Test
    fun count_returns_correct_number() {
        assertEquals(0, queries.count().executeAsOne())

        insertSampleDog(id = 1)
        insertSampleDog(id = 2, name = "Beagle")

        assertEquals(2, queries.count().executeAsOne())
    }

    @Test
    fun deleteAll_removes_all_records() {
        insertSampleDog(id = 1)
        insertSampleDog(id = 2, name = "Beagle")
        assertEquals(2, queries.count().executeAsOne())

        queries.deleteAll()

        assertEquals(0, queries.count().executeAsOne())
    }

    @Test
    fun insertDog_replaces_on_duplicate_id() {
        insertSampleDog(id = 1, name = "Old Name")
        insertSampleDog(id = 1, name = "New Name")

        val result = queries.getAll().executeAsList()
        assertEquals(1, result.size)
        assertEquals("New Name", result[0].name)
    }

    @Test
    fun getPaginated_returns_correct_page() {
        for (i in 1..10) {
            insertSampleDog(id = i.toLong(), name = "Dog$i")
        }

        val page0 = queries.getPaginated(3, 0).executeAsList()
        assertEquals(3, page0.size)
        assertEquals("Dog1", page0[0].name)
        assertEquals("Dog3", page0[2].name)

        val page1 = queries.getPaginated(3, 3).executeAsList()
        assertEquals(3, page1.size)
        assertEquals("Dog4", page1[0].name)

        val lastPage = queries.getPaginated(3, 9).executeAsList()
        assertEquals(1, lastPage.size)
        assertEquals("Dog10", lastPage[0].name)
    }

    @Test
    fun getPaginated_beyond_data_returns_empty() {
        insertSampleDog(id = 1)

        val result = queries.getPaginated(10, 100).executeAsList()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getByBreedGroup_filters_correctly() {
        insertSampleDog(id = 1, name = "Labrador", breedGroup = "Retrievers")
        insertSampleDog(id = 2, name = "German Shepherd", breedGroup = "Herding")
        insertSampleDog(id = 3, name = "Golden Retriever", breedGroup = "Retrievers")

        val retrievers = queries.getByBreedGroup("Retrievers").executeAsList()
        assertEquals(2, retrievers.size)
        assertEquals("Golden Retriever", retrievers[0].name)
        assertEquals("Labrador", retrievers[1].name)

        val herding = queries.getByBreedGroup("Herding").executeAsList()
        assertEquals(1, herding.size)
    }

    @Test
    fun getByBreedGroup_returns_empty_for_unknown_group() {
        insertSampleDog(id = 1, breedGroup = "Retrievers")

        val result = queries.getByBreedGroup("NonExistent").executeAsList()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getBySize_filters_correctly() {
        insertSampleDog(id = 1, name = "Chihuahua", sizeCategory = "Small")
        insertSampleDog(id = 2, name = "Labrador", sizeCategory = "Large")
        insertSampleDog(id = 3, name = "Pomeranian", sizeCategory = "Small")

        val small = queries.getBySize("Small").executeAsList()
        assertEquals(2, small.size)
    }

    @Test
    fun getAllBreedGroups_returns_distinct_non_empty_groups() {
        insertSampleDog(id = 1, breedGroup = "Retrievers")
        insertSampleDog(id = 2, breedGroup = "Herding")
        insertSampleDog(id = 3, breedGroup = "Retrievers")
        insertSampleDog(id = 4, breedGroup = "")

        val groups = queries.getAllBreedGroups().executeAsList()
        assertEquals(2, groups.size)
        assertTrue(groups.contains("Herding"))
        assertTrue(groups.contains("Retrievers"))
    }

    @Test
    fun getRandomBreedsWithWeight_filters_dogs_with_weight() {
        insertSampleDog(id = 1, name = "Heavy", maxWeightKg = 30.0)
        insertSampleDog(id = 2, name = "Weightless", maxWeightKg = 0.0)
        insertSampleDog(id = 3, name = "Light", maxWeightKg = 5.0)

        val result = queries.getRandomBreedsWithWeight(10).executeAsList()
        assertEquals(2, result.size)
        assertTrue(result.all { it.maxWeightKg > 0 })
    }

    @Test
    fun getRandomBreedsWithWeight_respects_limit() {
        for (i in 1..5) {
            insertSampleDog(id = i.toLong(), name = "Dog$i", maxWeightKg = i * 10.0)
        }

        val result = queries.getRandomBreedsWithWeight(2).executeAsList()
        assertEquals(2, result.size)
    }

    @Test
    fun getRandomBreedsWithDescription_filters_dogs_with_temperament() {
        insertSampleDog(id = 1, name = "Described", temperament = "Friendly")
        insertSampleDog(id = 2, name = "No Temperament", temperament = "")

        val result = queries.getRandomBreedsWithDescription(10).executeAsList()
        assertEquals(1, result.size)
        assertEquals("Described", result[0].name)
    }

    @Test
    fun getRandomBreedsWithFciGroup_filters_dogs_with_fci() {
        insertSampleDog(id = 1, name = "FCI Dog", fciGroup = 3)
        insertSampleDog(id = 2, name = "No FCI", fciGroup = 0)

        val result = queries.getRandomBreedsWithFciGroup(10).executeAsList()
        assertEquals(1, result.size)
        assertEquals("FCI Dog", result[0].name)
    }

    @Test
    fun getRandomBreedsWithCare_filters_dogs_with_care_fields() {
        insertSampleDog(id = 1, name = "NutritionDog", nutrition = "High protein")
        insertSampleDog(id = 2, name = "HygieneDog", hygiene = "Weekly bath")
        insertSampleDog(id = 3, name = "LossHairDog", lossHair = "Heavy shedding")
        insertSampleDog(id = 4, name = "NoCare")

        val result = queries.getRandomBreedsWithCare(10).executeAsList()
        assertEquals(3, result.size)
        val names = result.map { it.name }
        assertTrue(names.contains("NutritionDog"))
        assertTrue(names.contains("HygieneDog"))
        assertTrue(names.contains("LossHairDog"))
    }

    @Test
    fun getRandomBreedsWithCare_returns_empty_when_no_care_data() {
        insertSampleDog(id = 1, name = "NoCare")

        val result = queries.getRandomBreedsWithCare(10).executeAsList()
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertDog_preserves_all_fields() {
        queries.insertDog(
            id = 1, name = "TestDog", icon = "icon.png", origin = "Spain",
            breedGroup = "Terriers", temperament = "Bold, Brave",
            description = "A brave terrier", sizeCategory = "Small",
            minWeightKg = 3.5, maxWeightKg = 7.0,
            minHeightCm = 20.0, maxHeightCm = 30.0,
            lifeSpanMin = 12, lifeSpanMax = 16,
            coatType = "Wire", colors = "Black, White",
            exerciseNeeds = 7, groomingNeeds = 5,
            goodWithChildren = 6, goodWithOtherDogs = 4,
            trainability = 8, barkingLevel = 9,
            funFact = "Loves digging", images = "terrier.png",
            dataVersion = 2,
            nutrition = "Balanced diet", hygiene = "Regular grooming",
            lossHair = "Moderate", commonDiseases = "Allergies",
            otherNames = "Fox Terrier", fciGroup = 3,
            fciSection = 1, fciSectionType = "Large and medium Terriers"
        )

        val dog = queries.getById(1).executeAsOne()
        assertEquals("TestDog", dog.name)
        assertEquals("icon.png", dog.icon)
        assertEquals("Spain", dog.origin)
        assertEquals("Terriers", dog.breedGroup)
        assertEquals("Bold, Brave", dog.temperament)
        assertEquals("A brave terrier", dog.description)
        assertEquals("Small", dog.sizeCategory)
        assertEquals(3.5, dog.minWeightKg, 0.0001)
        assertEquals(7.0, dog.maxWeightKg, 0.0001)
        assertEquals(20.0, dog.minHeightCm, 0.0001)
        assertEquals(30.0, dog.maxHeightCm, 0.0001)
        assertEquals(12L, dog.lifeSpanMin)
        assertEquals(16L, dog.lifeSpanMax)
        assertEquals("Wire", dog.coatType)
        assertEquals("Black, White", dog.colors)
        assertEquals(7L, dog.exerciseNeeds)
        assertEquals(5L, dog.groomingNeeds)
        assertEquals(6L, dog.goodWithChildren)
        assertEquals(4L, dog.goodWithOtherDogs)
        assertEquals(8L, dog.trainability)
        assertEquals(9L, dog.barkingLevel)
        assertEquals("Loves digging", dog.funFact)
        assertEquals("terrier.png", dog.images)
        assertEquals(2L, dog.dataVersion)
        assertEquals("Balanced diet", dog.nutrition)
        assertEquals("Regular grooming", dog.hygiene)
        assertEquals("Moderate", dog.lossHair)
        assertEquals("Allergies", dog.commonDiseases)
        assertEquals("Fox Terrier", dog.otherNames)
        assertEquals(3L, dog.fciGroup)
        assertEquals(1L, dog.fciSection)
        assertEquals("Large and medium Terriers", dog.fciSectionType)
    }
}
