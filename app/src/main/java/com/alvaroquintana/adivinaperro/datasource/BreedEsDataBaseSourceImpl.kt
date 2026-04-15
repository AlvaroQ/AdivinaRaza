package com.alvaroquintana.adivinaperro.datasource

import android.util.Log
import com.alvaroquintana.adivinaperro.datasource.db.DogDao
import com.alvaroquintana.adivinaperro.datasource.db.SyncMetadata
import com.alvaroquintana.adivinaperro.datasource.db.SyncMetadataDao
import com.alvaroquintana.adivinaperro.datasource.db.toDomain
import com.alvaroquintana.adivinaperro.datasource.db.toEntity
import com.alvaroquintana.adivinaperro.utils.Constants
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.data.breedes.BreedEsMapper
import com.alvaroquintana.data.datasource.DataBaseSource
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
    private val dogDao: DogDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val firestore: FirebaseFirestore
) : DataBaseSource {

    private suspend fun isCacheFresh(): Boolean {
        val metadata = syncMetadataDao.getByCollection(Constants.SYNC_COLLECTION_BREEDS_ES) ?: return false
        val elapsed = System.currentTimeMillis() - metadata.lastSyncTimestamp
        return elapsed < Constants.STALE_THRESHOLD_MS
    }

    private suspend fun ensureSyncedIfNeeded() {
        val cachedCount = dogDao.count()
        val hasBreedEsMetadata = syncMetadataDao.getByCollection(Constants.SYNC_COLLECTION_BREEDS_ES) != null

        // Firestore breedES is now the single source for breeds.
        if (!hasBreedEsMetadata || cachedCount == 0 || !isCacheFresh()) {
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
            val entities = mutableListOf<com.alvaroquintana.adivinaperro.datasource.db.DogEntity>()

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
                    entities.add(dog.toEntity(id))
                }

                last = snapshot.documents.last()
            }

            if (entities.isNotEmpty()) {
                // Replace cache only when we have a valid mapped dataset.
                dogDao.deleteAll()
                dogDao.insertAll(entities)
                syncMetadataDao.upsert(
                    SyncMetadata(
                        collection = Constants.SYNC_COLLECTION_BREEDS_ES,
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                )
            } else {
                Log.e(
                    "BreedEsDataBaseSourceImpl",
                    "breedES sync fetched=$fetchedDocs mapped=0. Verify Firestore project/rules/collection."
                )
            }

            log(
                "BreedEsDataBaseSourceImpl",
                "breedES sync done fetched=$fetchedDocs mapped=${entities.size}"
            )
            entities.size
        } catch (e: Exception) {
            log("BreedEsDataBaseSourceImpl", "Failed to sync breedES", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("BreedEsDataBaseSourceImpl", "syncAllBreedsFromFirestore exception", e)
            0
        }
    }

    override suspend fun getBreedById(id: Int): Dog {
        ensureSyncedIfNeeded()

        val cached = dogDao.getById(id)
        if (cached != null) return cached.toDomain()

        val remoteDoc = fetchBreedDocumentById(id)
        val remoteData = remoteDoc?.data ?: return Dog()
        val remoteDog = BreedEsMapper.mapToDog(remoteDoc.id, remoteData)

        try {
            dogDao.insertAll(listOf(remoteDog.toEntity(resolveBreedId(remoteDoc) ?: id)))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return remoteDog
    }

    override suspend fun getBreedList(currentPage: Int): MutableList<Dog> {
        ensureSyncedIfNeeded()

        val offset = currentPage * Constants.TOTAL_ITEM_EACH_LOAD
        var cached = dogDao.getPaginated(Constants.TOTAL_ITEM_EACH_LOAD, offset)
        if (cached.isEmpty()) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) {
                cached = dogDao.getPaginated(Constants.TOTAL_ITEM_EACH_LOAD, offset)
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
        var result = dogDao.getRandomBreedsWithWeight(count).map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogDao.getRandomBreedsWithWeight(count).map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithWeight returned empty")
        }
        return trimmed
    }

    override suspend fun getRandomBreedsWithDescription(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogDao.getRandomBreedsWithDescription(count).map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogDao.getRandomBreedsWithDescription(count).map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithDescription returned empty")
        }
        return trimmed
    }

    override suspend fun getRandomBreedsWithFciGroup(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogDao.getRandomBreedsWithFciGroup(count).map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogDao.getRandomBreedsWithFciGroup(count).map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithFciGroup returned empty")
        }
        return trimmed
    }

    override suspend fun getRandomBreedsWithCare(count: Int): List<Dog> {
        ensureSyncedIfNeeded()
        var result = dogDao.getRandomBreedsWithCare(count).map { it.toDomain() }
        if (result.size < count) {
            val synced = syncAllBreedsFromFirestore()
            if (synced > 0) result = dogDao.getRandomBreedsWithCare(count).map { it.toDomain() }
        }
        val trimmed = result.take(count)
        if (trimmed.isEmpty()) {
            Log.e("BreedEsDataBaseSourceImpl", "getRandomBreedsWithCare returned empty")
        }
        return trimmed
    }

    override suspend fun getAppsRecommended(): MutableList<App> {
        // Not part of breedES source policy; keep legacy RTDB for apps.
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
