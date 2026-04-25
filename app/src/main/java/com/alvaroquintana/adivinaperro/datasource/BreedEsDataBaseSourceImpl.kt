package com.alvaroquintana.adivinaperro.datasource

import android.util.Log
import com.alvaroquintana.adivinaperro.utils.Constants
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.data.breedes.BreedEsMapper
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.data.db.AdivinaRazaDatabase
import com.alvaroquintana.data.db.toDomain
import com.alvaroquintana.domain.App
import com.alvaroquintana.domain.Dog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BreedEsDataBaseSourceImpl(
    private val database: AdivinaRazaDatabase,
    private val firestore: FirebaseFirestore
) : DataBaseSource {

    private val dogsQueries get() = database.dogsQueries
    private val syncQueries get() = database.syncMetadataQueries

    private fun isCacheFresh(): Boolean {
        val metadata = syncQueries.getByCollection(Constants.SYNC_COLLECTION_BREEDS_ES)
            .executeAsOneOrNull() ?: return false
        val elapsed = System.currentTimeMillis() - metadata.lastSyncTimestamp
        return elapsed < Constants.STALE_THRESHOLD_MS
    }

    private suspend fun ensureSyncedIfNeeded() {
        val cachedCount = dogsQueries.count().executeAsOne()
        val hasMetadata = syncQueries.getByCollection(Constants.SYNC_COLLECTION_BREEDS_ES)
            .executeAsOneOrNull() != null

        if (!hasMetadata || cachedCount == 0L || !isCacheFresh()) {
            syncAllBreedsFromFirestore()
        }
    }

    private fun parseBreedId(value: Any?): Int? {
        return when (value) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull()
            else -> null
        }
    }

    private fun resolveBreedId(doc: DocumentSnapshot): Int? {
        return parseBreedId(doc.id)
            ?: parseBreedId(doc.get("breedId"))
            ?: parseBreedId(doc.get("id"))
    }

    private suspend fun fetchBreedDocumentById(id: Int): DocumentSnapshot? {
        val collection = firestore.collection(Constants.COLLECTION_BREEDS_ES)

        val direct = suspendCancellableCoroutine<DocumentSnapshot?> { continuation ->
            collection.document(id.toString())
                .get()
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener {
                    FirebaseCrashlytics.getInstance().recordException(it)
                    continuation.resume(null)
                }
        }

        if (direct?.data != null) return direct

        val fieldCandidates = listOf("breedId", "id")
        val valueCandidates: List<Any> = listOf(id, id.toString())

        for (field in fieldCandidates) {
            for (value in valueCandidates) {
                val querySnapshot = suspendCancellableCoroutine<com.google.firebase.firestore.QuerySnapshot?> { continuation ->
                    collection.whereEqualTo(field, value)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { continuation.resume(it) }
                        .addOnFailureListener {
                            FirebaseCrashlytics.getInstance().recordException(it)
                            continuation.resume(null)
                        }
                }

                val doc = querySnapshot?.documents?.firstOrNull()
                if (doc?.data != null) return doc
            }
        }

        return null
    }

    private suspend fun syncAllBreedsFromFirestore(): Int {
        return try {
            val pageSize = 500L
            var last: DocumentSnapshot? = null
            var fetchedDocs = 0
            data class MappedBreed(val id: Int, val dog: Dog)
            val mapped = mutableListOf<MappedBreed>()

            while (true) {
                var query: Query = firestore
                    .collection(Constants.COLLECTION_BREEDS_ES)
                    .orderBy(FieldPath.documentId())
                    .limit(pageSize)

                if (last != null) query = query.startAfter(last)

                val snapshot = suspendCancellableCoroutine { continuation ->
                    query.get()
                        .addOnSuccessListener { continuation.resume(it) }
                        .addOnFailureListener {
                            FirebaseCrashlytics.getInstance().recordException(it)
                            continuation.resume(null)
                        }
                }

                if (snapshot == null || snapshot.isEmpty) break

                fetchedDocs += snapshot.size()
                for (doc in snapshot.documents) {
                    val id = resolveBreedId(doc) ?: continue
                    val data = doc.data ?: continue
                    val dog = BreedEsMapper.mapToDog(doc.id, data)
                    mapped.add(MappedBreed(id, dog))
                }

                last = snapshot.documents.last()
            }

            if (mapped.isNotEmpty()) {
                database.transaction {
                    dogsQueries.deleteAll()
                    for (breed in mapped) {
                        val d = breed.dog
                        dogsQueries.insertDog(
                            id = breed.id.toLong(), name = d.name, icon = d.icon,
                            origin = d.origin, breedGroup = d.breedGroup,
                            temperament = d.temperament, description = d.description,
                            sizeCategory = d.sizeCategory,
                            minWeightKg = d.minWeightKg, maxWeightKg = d.maxWeightKg,
                            minHeightCm = d.minHeightCm, maxHeightCm = d.maxHeightCm,
                            lifeSpanMin = d.lifeSpanMin.toLong(), lifeSpanMax = d.lifeSpanMax.toLong(),
                            coatType = d.coatType, colors = d.colors,
                            exerciseNeeds = d.exerciseNeeds.toLong(),
                            groomingNeeds = d.groomingNeeds.toLong(),
                            goodWithChildren = d.goodWithChildren.toLong(),
                            goodWithOtherDogs = d.goodWithOtherDogs.toLong(),
                            trainability = d.trainability.toLong(),
                            barkingLevel = d.barkingLevel.toLong(),
                            funFact = d.funFact, images = d.images,
                            dataVersion = d.dataVersion.toLong(),
                            nutrition = d.nutrition, hygiene = d.hygiene,
                            lossHair = d.lossHair, commonDiseases = d.commonDiseases,
                            otherNames = d.otherNames,
                            fciGroup = d.fciGroup.toLong(), fciSection = d.fciSection.toLong(),
                            fciSectionType = d.fciSectionType
                        )
                    }
                    syncQueries.upsert(
                        collection = Constants.SYNC_COLLECTION_BREEDS_ES,
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                }
            } else {
                Log.e(
                    "BreedEsDataBaseSourceImpl",
                    "breedES sync fetched=$fetchedDocs mapped=0. Verify Firestore project/rules/collection."
                )
            }

            log(
                "BreedEsDataBaseSourceImpl",
                "breedES sync done fetched=$fetchedDocs mapped=${mapped.size}"
            )
            mapped.size
        } catch (e: Exception) {
            log("BreedEsDataBaseSourceImpl", "Failed to sync breedES", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("BreedEsDataBaseSourceImpl", "syncAllBreedsFromFirestore exception", e)
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

        val remoteDoc = fetchBreedDocumentById(id)
        val remoteData = remoteDoc?.data ?: return Dog()
        val remoteDog = BreedEsMapper.mapToDog(remoteDoc.id, remoteData)

        try {
            insertDog(resolveBreedId(remoteDoc) ?: id, remoteDog)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return remoteDog
    }

    override suspend fun getBreedList(currentPage: Int): MutableList<Dog> {
        ensureSyncedIfNeeded()

        val limit = Constants.TOTAL_ITEM_EACH_LOAD.toLong()
        val offset = (currentPage * Constants.TOTAL_ITEM_EACH_LOAD).toLong()
        var cached = dogsQueries.getPaginated(limit, offset).executeAsList()
        if (cached.isEmpty()) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) {
                cached = dogsQueries.getPaginated(limit, offset).executeAsList()
            }
        }
        val result = cached.map { it.toDomain() }.toMutableList()
        if (result.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getBreedList page=$currentPage returned empty after sync attempts")
        }
        return result
    }

    override suspend fun getRandomBreedsWithWeight(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogsQueries.getRandomBreedsWithWeight(count.toLong()).executeAsList().map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogsQueries.getRandomBreedsWithWeight(count.toLong()).executeAsList().map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithWeight returned empty")
        }
        return trimmed
    }

    override suspend fun getRandomBreedsWithDescription(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogsQueries.getRandomBreedsWithDescription(count.toLong()).executeAsList().map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogsQueries.getRandomBreedsWithDescription(count.toLong()).executeAsList().map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithDescription returned empty")
        }
        return trimmed
    }

    override suspend fun getRandomBreedsWithFciGroup(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogsQueries.getRandomBreedsWithFciGroup(count.toLong()).executeAsList().map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogsQueries.getRandomBreedsWithFciGroup(count.toLong()).executeAsList().map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithFciGroup returned empty")
        }
        return trimmed
    }

    override suspend fun getRandomBreedsWithCare(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogsQueries.getRandomBreedsWithCare(count.toLong()).executeAsList().map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogsQueries.getRandomBreedsWithCare(count.toLong()).executeAsList().map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithCare returned empty")
        }
        return trimmed
    }

    override suspend fun getAppsRecommended(): MutableList<App> {
        return suspendCancellableCoroutine { continuation ->
            val reference = FirebaseDatabase.getInstance().getReference(Constants.PATH_REFERENCE_APPS)
            val listener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val appList = mutableListOf<App>()
                        if (dataSnapshot.hasChildren()) {
                            for (snapshot in dataSnapshot.children) {
                                val app = snapshot.getValue(App::class.java)
                                if (app != null) {
                                    appList.add(app)
                                }
                            }
                        }
                        continuation.resume(appList)
                    } catch (e: Exception) {
                        log("BreedEsDataBaseSourceImpl", "Failed to parse apps list.", e)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        continuation.resume(mutableListOf())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    log("BreedEsDataBaseSourceImpl", "Failed to read apps from RTDB.", error.toException())
                    continuation.resume(mutableListOf())
                    FirebaseCrashlytics.getInstance().recordException(error.toException())
                }
            }

            reference.addListenerForSingleValueEvent(listener)
            continuation.invokeOnCancellation { reference.removeEventListener(listener) }
        }
    }
}
