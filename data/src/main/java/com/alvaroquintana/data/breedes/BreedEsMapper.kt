package com.alvaroquintana.data.breedes

import com.alvaroquintana.domain.Dog
import kotlin.math.max
import kotlin.math.min

object BreedEsMapper {

    fun mapToDog(docId: String, data: Map<String, Any?>): Dog {
        val name = data.string("name")
        val image = data.string("image")

        val shortDescription = data.string("shortDescription")

        val physical = data.map("physicalCharacteristics")
        val physicalDescription = physical.string("description")
        val colorHair = physical.string("colorHair")

        val fci = data.map("fci")
        val fciGroupType = fci.string("groupType")
        val fciGroup = fci.number("group")?.toInt() ?: 0
        val fciSection = fci.number("section")?.toInt() ?: 0
        val fciSectionType = fci.string("sectionType")

        val main = data.map("mainInformation")
        val sizeBreedEs = main.string("sizeBreed")
        val character = main.list("character").mapNotNull { it as? String }.filter { it.isNotBlank() }
        val typeHairDescription = main.string("typeHairDescription")
        val lifeExpectancy = main.map("lifeExpectancy").number("expectancy")?.toInt() ?: 0

        val weight = physical.map("weight")
        val height = physical.map("height")

        val (minWeightKg, maxWeightKg) = combineSexRanges(weight)
        val (minHeightCm, maxHeightCm) = combineSexRanges(height)

        val otherNames = (data["otherNames"] as? List<*>)?.mapNotNull { it as? String }?.filter { it.isNotBlank() }.orEmpty()
        val commonDiseases = (data["commonDiseases"] as? List<*>)?.mapNotNull { it as? String }?.filter { it.isNotBlank() }.orEmpty()

        val nutrition = data.string("nutrition")
        val hygiene = data.string("hygiene")
        val lossHair = data.string("lossHair")

        val description = when {
            shortDescription.isNotBlank() -> shortDescription
            physicalDescription.isNotBlank() -> physicalDescription
            else -> ""
        }

        return Dog(
            icon = image,
            images = image,
            name = name,
            description = description,
            temperament = character.joinToString(", "),
            breedGroup = fciGroupType,
            sizeCategory = mapSizeCategory(sizeBreedEs),
            minWeightKg = minWeightKg,
            maxWeightKg = maxWeightKg,
            minHeightCm = minHeightCm,
            maxHeightCm = maxHeightCm,
            lifeSpanMin = lifeExpectancy,
            lifeSpanMax = lifeExpectancy,
            coatType = typeHairDescription,
            colors = colorHair,
            funFact = otherNames.firstOrNull().orEmpty(),
            dataVersion = 1,
            nutrition = nutrition,
            hygiene = hygiene,
            lossHair = lossHair,
            commonDiseases = commonDiseases.joinToString(", "),
            otherNames = otherNames.joinToString(", "),
            fciGroup = fciGroup,
            fciSection = fciSection,
            fciSectionType = fciSectionType
        )
    }

    private fun mapSizeCategory(value: String): String {
        return when (value.trim().lowercase()) {
            "pequeño", "pequeño", "pequeno" -> "Small"
            "mediano" -> "Medium"
            "grande" -> "Large"
            "gigante" -> "Giant"
            else -> value.trim()
        }
    }

    private fun combineSexRanges(metric: Map<String, Any?>): Pair<Double, Double> {
        val male = metric.list("macho")
        val female = metric.list("hembra")

        val (maleMin, maleMax) = minMaxFromList(male)
        val (femaleMin, femaleMax) = minMaxFromList(female)

        val overallMin = min(maleMin, femaleMin)
        val overallMax = max(maleMax, femaleMax)

        if (overallMax <= 0.0) return 0.0 to 0.0
        return overallMin to overallMax
    }

    private fun minMaxFromList(values: List<Any?>): Pair<Double, Double> {
        if (values.isEmpty()) return 0.0 to 0.0

        val first = (values.getOrNull(0) as? Number)?.toDouble() ?: 0.0
        val second = (values.getOrNull(1) as? Number)?.toDouble() ?: first

        val minV = min(first, second)
        val maxV = max(first, second)

        return minV to maxV
    }

    private fun Map<String, Any?>.string(key: String): String = this[key] as? String ?: ""

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.map(key: String): Map<String, Any?> = (this[key] as? Map<*, *>)
        ?.entries
        ?.associate { (k, v) -> k.toString() to v }
        ?: emptyMap()

    private fun Map<String, Any?>.list(key: String): List<Any?> = this[key] as? List<Any?> ?: emptyList()

    private fun Map<String, Any?>.number(key: String): Number? = this[key] as? Number
}
