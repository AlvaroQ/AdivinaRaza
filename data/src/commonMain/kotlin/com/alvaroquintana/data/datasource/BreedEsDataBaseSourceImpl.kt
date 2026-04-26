package com.alvaroquintana.data.datasource

import com.alvaroquintana.data.breedes.BreedEsMapper
import com.alvaroquintana.data.db.AdivinaRazaDatabase
import com.alvaroquintana.data.db.toDomain
import com.alvaroquintana.domain.App
import com.alvaroquintana.domain.Dog
import dev.gitlive.firebase.crashlytics.FirebaseCrashlytics
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.longOrNull

private const val COLLECTION_BREEDS_ES = "breedES"
private const val SYNC_COLLECTION_BREEDS_ES = "breeds_es"
private const val STALE_THRESHOLD_MS = 86_400_000L
private const val PAGE_SIZE = 500
private const val TOTAL_ITEM_EACH_LOAD = 15

class BreedEsDataBaseSourceImpl(
    private val database: AdivinaRazaDatabase,
    private val firestore: FirebaseFirestore?,
    private val crashlytics: FirebaseCrashlytics?
) : DataBaseSource {

    private val dogsQueries get() = database.dogsQueries
    private val syncQueries get() = database.syncMetadataQueries

    private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()

    private fun recordException(t: Throwable) {
        crashlytics?.let { runCatching { it.recordException(t) } }
    }

    private fun isCacheFresh(): Boolean {
        val metadata = syncQueries.getByCollection(SYNC_COLLECTION_BREEDS_ES)
            .executeAsOneOrNull() ?: return false
        return (nowMs() - metadata.lastSyncTimestamp) < STALE_THRESHOLD_MS
    }

    private suspend fun ensureSyncedIfNeeded() {
        if (firestore == null) return // Firebase not configured (e.g. iOS without GoogleService-Info.plist)
        val cachedCount = dogsQueries.count().executeAsOne()
        val hasMetadata = syncQueries.getByCollection(SYNC_COLLECTION_BREEDS_ES)
            .executeAsOneOrNull() != null
        if (!hasMetadata || cachedCount == 0L || !isCacheFresh()) {
            syncAllBreedsFromFirestore()
        }
    }

    private fun parseBreedId(value: Any?): Int? = when (value) {
        is Number -> value.toInt()
        is String -> value.toIntOrNull()
        else -> null
    }

    private fun resolveBreedId(id: String, data: Map<String, Any?>): Int? =
        parseBreedId(id) ?: parseBreedId(data["breedId"]) ?: parseBreedId(data["id"])

    private suspend fun fetchBreedDocumentById(id: Int): Pair<String, Map<String, Any?>>? {
        val fs = firestore ?: return null
        return runCatching {
            val doc = fs.collection(COLLECTION_BREEDS_ES).document(id.toString()).get()
            val data = doc.dataAsMap()
            if (data.isNotEmpty()) doc.id to data else null
        }.onFailure { recordException(it) }.getOrNull()
    }

    private suspend fun syncAllBreedsFromFirestore(): Int {
        val fs = firestore ?: return 0
        return try {
            data class MappedBreed(val id: Int, val dog: Dog)
            val mapped = mutableListOf<MappedBreed>()
            val snapshot = fs.collection(COLLECTION_BREEDS_ES)
                .limit(PAGE_SIZE)
                .get()
            for (doc in snapshot.documents) {
                val data = doc.dataAsMap()
                if (data.isEmpty()) continue
                val parsedId = resolveBreedId(doc.id, data) ?: continue
                mapped.add(MappedBreed(parsedId, BreedEsMapper.mapToDog(doc.id, data)))
            }
            if (mapped.isNotEmpty()) {
                database.transaction {
                    dogsQueries.deleteAll()
                    for (breed in mapped) insertDog(breed.id, breed.dog)
                    syncQueries.upsert(
                        collection = SYNC_COLLECTION_BREEDS_ES,
                        lastSyncTimestamp = nowMs()
                    )
                }
            }
            mapped.size
        } catch (e: Throwable) {
            recordException(e)
            0
        }
    }

    private fun insertDog(id: Int, dog: Dog) {
        dogsQueries.insertDog(
            id = id.toLong(), name = dog.name, icon = dog.icon,
            origin = dog.origin, breedGroup = dog.breedGroup,
            temperament = dog.temperament, description = dog.description,
            sizeCategory = dog.sizeCategory,
            minWeightKg = dog.minWeightKg, maxWeightKg = dog.maxWeightKg,
            minHeightCm = dog.minHeightCm, maxHeightCm = dog.maxHeightCm,
            lifeSpanMin = dog.lifeSpanMin.toLong(), lifeSpanMax = dog.lifeSpanMax.toLong(),
            coatType = dog.coatType, colors = dog.colors,
            exerciseNeeds = dog.exerciseNeeds.toLong(),
            groomingNeeds = dog.groomingNeeds.toLong(),
            goodWithChildren = dog.goodWithChildren.toLong(),
            goodWithOtherDogs = dog.goodWithOtherDogs.toLong(),
            trainability = dog.trainability.toLong(),
            barkingLevel = dog.barkingLevel.toLong(),
            funFact = dog.funFact, images = dog.images,
            dataVersion = dog.dataVersion.toLong(),
            nutrition = dog.nutrition, hygiene = dog.hygiene,
            lossHair = dog.lossHair, commonDiseases = dog.commonDiseases,
            otherNames = dog.otherNames,
            fciGroup = dog.fciGroup.toLong(), fciSection = dog.fciSection.toLong(),
            fciSectionType = dog.fciSectionType
        )
    }

    override suspend fun getBreedById(id: Int): Dog {
        ensureSyncedIfNeeded()
        val cached = dogsQueries.getById(id.toLong()).executeAsOneOrNull()
        if (cached != null) return cached.toDomain()

        val (docId, data) = fetchBreedDocumentById(id) ?: return Dog()
        val remoteDog = BreedEsMapper.mapToDog(docId, data)
        runCatching { insertDog(resolveBreedId(docId, data) ?: id, remoteDog) }
            .onFailure { recordException(it) }
        return remoteDog
    }

    override suspend fun getBreedList(currentPage: Int): MutableList<Dog> {
        ensureSyncedIfNeeded()
        val limit = TOTAL_ITEM_EACH_LOAD.toLong()
        val offset = (currentPage * TOTAL_ITEM_EACH_LOAD).toLong()
        var cached = dogsQueries.getPaginated(limit, offset).executeAsList()
        if (cached.isEmpty() && syncAllBreedsFromFirestore() > 0) {
            cached = dogsQueries.getPaginated(limit, offset).executeAsList()
        }
        return cached.map { it.toDomain() }.toMutableList()
    }

    override suspend fun getRandomBreedsWithWeight(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var rows = dogsQueries.getRandomBreedsWithWeight(count.toLong()).executeAsList()
        if (rows.size < count && syncAllBreedsFromFirestore() > 0) {
            rows = dogsQueries.getRandomBreedsWithWeight(count.toLong()).executeAsList()
        }
        return rows.take(count).map { it.toDomain() }
    }

    override suspend fun getRandomBreedsWithDescription(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var rows = dogsQueries.getRandomBreedsWithDescription(count.toLong()).executeAsList()
        if (rows.size < count && syncAllBreedsFromFirestore() > 0) {
            rows = dogsQueries.getRandomBreedsWithDescription(count.toLong()).executeAsList()
        }
        return rows.take(count).map { it.toDomain() }
    }

    override suspend fun getRandomBreedsWithFciGroup(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var rows = dogsQueries.getRandomBreedsWithFciGroup(count.toLong()).executeAsList()
        if (rows.size < count && syncAllBreedsFromFirestore() > 0) {
            rows = dogsQueries.getRandomBreedsWithFciGroup(count.toLong()).executeAsList()
        }
        return rows.take(count).map { it.toDomain() }
    }

    override suspend fun getRandomBreedsWithCare(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var rows = dogsQueries.getRandomBreedsWithCare(count.toLong()).executeAsList()
        if (rows.size < count && syncAllBreedsFromFirestore() > 0) {
            rows = dogsQueries.getRandomBreedsWithCare(count.toLong()).executeAsList()
        }
        return rows.take(count).map { it.toDomain() }
    }

    /**
     * RTDB-backed recommended apps list. Phase 4b will plug in the gitlive
     * Realtime Database client; for now this returns an empty list so the
     * Info screen renders cleanly without recommended apps.
     */
    override suspend fun getAppsRecommended(): MutableList<App> = mutableListOf()
}

private fun DocumentSnapshot.dataAsMap(): Map<String, Any?> {
    val element = runCatching { data(JsonElement.serializer()) }.getOrNull() as? JsonObject
        ?: return emptyMap()
    return element.toAnyMap()
}

private fun JsonObject.toAnyMap(): Map<String, Any?> = mapValues { (_, v) -> v.toAny() }

private fun JsonElement.toAny(): Any? = when (this) {
    is JsonPrimitive -> when {
        isString -> contentOrNull
        booleanOrNull != null -> boolean
        longOrNull != null -> longOrNull
        doubleOrNull != null -> doubleOrNull
        else -> contentOrNull
    }
    is JsonObject -> toAnyMap()
    else -> jsonArray.map { it.toAny() }
}
