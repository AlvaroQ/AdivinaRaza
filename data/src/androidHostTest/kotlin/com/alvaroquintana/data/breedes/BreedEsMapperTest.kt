package com.alvaroquintana.data.breedes

import com.alvaroquintana.domain.Dog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreedEsMapperTest {

    @Test
    fun mapToDog_maps_basic_fields_and_ranges() {
        val doc = mapOf(
            "name" to "Affenpinscher",
            "image" to "https://example.com/dog.jpg",
            "shortDescription" to "Desc breve",
            "nutrition" to "Nutrición",
            "hygiene" to "Higiene",
            "lossHair" to "Muda",
            "otherNames" to listOf("Perro mono"),
            "commonDiseases" to listOf("Otitis"),
            "fci" to mapOf(
                "groupType" to "Terriers",
                "group" to 3,
                "section" to 1,
                "sectionType" to "Terriers de talla grande y media"
            ),
            "mainInformation" to mapOf(
                "sizeBreed" to "Pequeño",
                "character" to listOf("Juguetón", "Cazador"),
                "typeHairDescription" to "Duro",
                "lifeExpectancy" to mapOf(
                    "expectancy" to 13,
                    "measure" to "años"
                )
            ),
            "physicalCharacteristics" to mapOf(
                "colorHair" to "Negro",
                "weight" to mapOf(
                    "medida" to "kg",
                    "macho" to listOf(4, 6),
                    "hembra" to listOf(4, 6)
                ),
                "height" to mapOf(
                    "medida" to "cm",
                    "macho" to listOf(25, 30),
                    "hembra" to listOf(25, 30)
                )
            )
        )

        val dog: Dog = BreedEsMapper.mapToDog("0", doc)

        assertEquals("Affenpinscher", dog.name)
        assertEquals("https://example.com/dog.jpg", dog.icon)
        assertEquals("Desc breve", dog.description)
        assertEquals("Small", dog.sizeCategory)

        assertEquals(4.0, dog.minWeightKg, 0.0001)
        assertEquals(6.0, dog.maxWeightKg, 0.0001)
        assertEquals(25.0, dog.minHeightCm, 0.0001)
        assertEquals(30.0, dog.maxHeightCm, 0.0001)

        assertEquals(13, dog.lifeSpanMin)
        assertEquals(13, dog.lifeSpanMax)

        assertTrue(dog.temperament.contains("Juguetón"))
        assertEquals("Terriers", dog.breedGroup)

        assertEquals("Nutrición", dog.nutrition)
        assertEquals("Higiene", dog.hygiene)
        assertEquals("Muda", dog.lossHair)

        assertEquals(3, dog.fciGroup)
        assertEquals(1, dog.fciSection)
        assertEquals("Terriers de talla grande y media", dog.fciSectionType)
    }

    @Test
    fun mapToDog_falls_back_to_physical_description_when_shortDescription_missing() {
        val doc = mapOf(
            "name" to "X",
            "image" to "https://example.com/x.jpg",
            "shortDescription" to "",
            "physicalCharacteristics" to mapOf(
                "description" to "Descripcion fisica",
                "weight" to mapOf("macho" to listOf(1, 2)),
                "height" to mapOf("macho" to listOf(10, 12))
            )
        )

        val dog = BreedEsMapper.mapToDog("1", doc)
        assertEquals("Descripcion fisica", dog.description)
    }

    @Test
    fun mapToDog_handles_missing_female_range() {
        val doc = mapOf(
            "name" to "Y",
            "image" to "https://example.com/y.jpg",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf(
                "sizeBreed" to "Pequeño"
            ),
            "physicalCharacteristics" to mapOf(
                "weight" to mapOf("macho" to listOf(4, 6)),
                "height" to mapOf("macho" to listOf(25, 30))
            )
        )

        val dog = BreedEsMapper.mapToDog("2", doc)
        assertEquals(4.0, dog.minWeightKg, 0.0001)
        assertEquals(6.0, dog.maxWeightKg, 0.0001)
        assertEquals("Small", dog.sizeCategory)
    }

    @Test
    fun mapToDog_maps_size_with_accent_variants() {
        val doc = mapOf(
            "name" to "Z",
            "image" to "https://example.com/z.jpg",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf(
                "sizeBreed" to "Pequeño"
            )
        )

        val dog = BreedEsMapper.mapToDog("3", doc)
        assertEquals("Small", dog.sizeCategory)
    }
}
