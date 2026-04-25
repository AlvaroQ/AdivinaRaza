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
                "sizeBreed" to "Pequeño"
            )
        )

        val dog = BreedEsMapper.mapToDog("3", doc)
        assertEquals("Small", dog.sizeCategory)
    }

    @Test
    fun mapToDog_maps_mediano_to_medium() {
        val doc = mapOf(
            "name" to "M",
            "image" to "",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf("sizeBreed" to "Mediano")
        )

        assertEquals("Medium", BreedEsMapper.mapToDog("4", doc).sizeCategory)
    }

    @Test
    fun mapToDog_maps_grande_to_large() {
        val doc = mapOf(
            "name" to "L",
            "image" to "",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf("sizeBreed" to "Grande")
        )

        assertEquals("Large", BreedEsMapper.mapToDog("5", doc).sizeCategory)
    }

    @Test
    fun mapToDog_maps_gigante_to_giant() {
        val doc = mapOf(
            "name" to "XL",
            "image" to "",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf("sizeBreed" to "Gigante")
        )

        assertEquals("Giant", BreedEsMapper.mapToDog("6", doc).sizeCategory)
    }

    @Test
    fun mapToDog_keeps_unknown_size_as_is() {
        val doc = mapOf(
            "name" to "U",
            "image" to "",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf("sizeBreed" to "Desconocido")
        )

        assertEquals("Desconocido", BreedEsMapper.mapToDog("7", doc).sizeCategory)
    }

    @Test
    fun mapToDog_combines_male_and_female_ranges() {
        val doc = mapOf(
            "name" to "Combo",
            "image" to "",
            "shortDescription" to "Desc",
            "physicalCharacteristics" to mapOf(
                "weight" to mapOf(
                    "macho" to listOf(10, 15),
                    "hembra" to listOf(8, 12)
                ),
                "height" to mapOf(
                    "macho" to listOf(50, 60),
                    "hembra" to listOf(45, 55)
                )
            )
        )

        val dog = BreedEsMapper.mapToDog("8", doc)
        assertEquals(8.0, dog.minWeightKg, 0.0001)
        assertEquals(15.0, dog.maxWeightKg, 0.0001)
        assertEquals(45.0, dog.minHeightCm, 0.0001)
        assertEquals(60.0, dog.maxHeightCm, 0.0001)
    }

    @Test
    fun mapToDog_uses_female_only_when_male_missing() {
        val doc = mapOf(
            "name" to "F",
            "image" to "",
            "shortDescription" to "Desc",
            "physicalCharacteristics" to mapOf(
                "weight" to mapOf(
                    "hembra" to listOf(5, 8)
                ),
                "height" to mapOf(
                    "hembra" to listOf(20, 25)
                )
            )
        )

        val dog = BreedEsMapper.mapToDog("9", doc)
        assertEquals(5.0, dog.minWeightKg, 0.0001)
        assertEquals(8.0, dog.maxWeightKg, 0.0001)
        assertEquals(20.0, dog.minHeightCm, 0.0001)
        assertEquals(25.0, dog.maxHeightCm, 0.0001)
    }

    @Test
    fun mapToDog_handles_completely_empty_data() {
        val doc = emptyMap<String, Any?>()

        val dog = BreedEsMapper.mapToDog("10", doc)

        assertEquals("", dog.name)
        assertEquals("", dog.icon)
        assertEquals("", dog.description)
        assertEquals(0.0, dog.minWeightKg, 0.0001)
        assertEquals(0.0, dog.maxWeightKg, 0.0001)
        assertEquals(0, dog.fciGroup)
        assertEquals("", dog.sizeCategory)
    }

    @Test
    fun mapToDog_joins_other_names_with_comma() {
        val doc = mapOf(
            "name" to "Multi",
            "image" to "",
            "shortDescription" to "Desc",
            "otherNames" to listOf("Alias1", "Alias2", "Alias3")
        )

        val dog = BreedEsMapper.mapToDog("11", doc)
        assertEquals("Alias1, Alias2, Alias3", dog.otherNames)
        assertEquals("Alias1", dog.funFact)
    }

    @Test
    fun mapToDog_joins_common_diseases_with_comma() {
        val doc = mapOf(
            "name" to "Sick",
            "image" to "",
            "shortDescription" to "Desc",
            "commonDiseases" to listOf("Hip Dysplasia", "Cataracts")
        )

        val dog = BreedEsMapper.mapToDog("12", doc)
        assertEquals("Hip Dysplasia, Cataracts", dog.commonDiseases)
    }

    @Test
    fun mapToDog_filters_blank_entries_from_lists() {
        val doc = mapOf(
            "name" to "Filtered",
            "image" to "",
            "shortDescription" to "Desc",
            "otherNames" to listOf("Valid", "", "  ", "AlsoValid"),
            "commonDiseases" to listOf("", "Disease", "  ")
        )

        val dog = BreedEsMapper.mapToDog("13", doc)
        assertEquals("Valid, AlsoValid", dog.otherNames)
        assertEquals("Disease", dog.commonDiseases)
    }

    @Test
    fun mapToDog_joins_character_traits_as_temperament() {
        val doc = mapOf(
            "name" to "Temper",
            "image" to "",
            "shortDescription" to "Desc",
            "mainInformation" to mapOf(
                "character" to listOf("Loyal", "Brave", "Calm")
            )
        )

        val dog = BreedEsMapper.mapToDog("14", doc)
        assertEquals("Loyal, Brave, Calm", dog.temperament)
    }

    @Test
    fun mapToDog_sets_zero_weight_and_height_when_no_physical_data() {
        val doc = mapOf(
            "name" to "NoPhysical",
            "image" to "",
            "shortDescription" to "Desc"
        )

        val dog = BreedEsMapper.mapToDog("15", doc)
        assertEquals(0.0, dog.minWeightKg, 0.0001)
        assertEquals(0.0, dog.maxWeightKg, 0.0001)
        assertEquals(0.0, dog.minHeightCm, 0.0001)
        assertEquals(0.0, dog.maxHeightCm, 0.0001)
    }
}
