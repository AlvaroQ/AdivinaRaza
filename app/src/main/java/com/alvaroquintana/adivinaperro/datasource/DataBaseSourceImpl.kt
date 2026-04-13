package com.alvaroquintana.adivinaperro.datasource

import com.alvaroquintana.adivinaperro.utils.Constants.PATH_REFERENCE_BREEDS
import com.alvaroquintana.adivinaperro.utils.Constants.PATH_REFERENCE_APPS
import com.alvaroquintana.adivinaperro.utils.Constants.STALE_THRESHOLD_MS
import com.alvaroquintana.adivinaperro.utils.Constants.SYNC_COLLECTION_BREEDS
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_ITEM_EACH_LOAD
import com.alvaroquintana.adivinaperro.datasource.db.DogDao
import com.alvaroquintana.adivinaperro.datasource.db.SyncMetadata
import com.alvaroquintana.adivinaperro.datasource.db.SyncMetadataDao
import com.alvaroquintana.adivinaperro.datasource.db.toDomain
import com.alvaroquintana.adivinaperro.datasource.db.toEntity
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.domain.App
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.ExperimentalCoroutinesApi

class DataBaseSourceImpl(
    private val dogDao: DogDao,
    private val syncMetadataDao: SyncMetadataDao
) : DataBaseSource {

    private suspend fun isCacheFresh(): Boolean {
        val metadata = syncMetadataDao.getByCollection(SYNC_COLLECTION_BREEDS) ?: return false
        val elapsed = System.currentTimeMillis() - metadata.lastSyncTimestamp
        return elapsed < STALE_THRESHOLD_MS
    }

    override suspend fun getBreedById(id: Int): Dog {
        // Try Room cache first
        if (isCacheFresh()) {
            val cached = dogDao.getById(id)
            if (cached != null) return cached.toDomain()
        }

        // Fallback to Firebase
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_BREEDS + id)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        continuation.resume(dataSnapshot.getValue(Dog::class.java) as Dog)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("getBreedById FAILED", "Failed to read value.", error.toException())
                        continuation.resume(Dog())
                        FirebaseCrashlytics.getInstance().recordException(error.toException())
                    }
                })
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getBreedList(currentPage: Int): MutableList<Dog> {
        // Try Room cache first (paginated)
        if (isCacheFresh()) {
            val offset = currentPage * TOTAL_ITEM_EACH_LOAD
            val cached = dogDao.getPaginated(TOTAL_ITEM_EACH_LOAD, offset)
            if (cached.isNotEmpty()) return cached.map { it.toDomain() }.toMutableList()
        }

        // Fallback to Firebase
        val firebaseResult = fetchBreedListFromFirebase(currentPage)

        // Cache the fetched page in Room
        if (firebaseResult.isNotEmpty()) {
            try {
                val startId = currentPage * TOTAL_ITEM_EACH_LOAD
                val entities = firebaseResult.mapIndexed { index, dog ->
                    dog.toEntity(startId + index)
                }
                dogDao.insertAll(entities)
                syncMetadataDao.upsert(
                    SyncMetadata(
                        collection = SYNC_COLLECTION_BREEDS,
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                log("DataBaseSourceImpl", "Failed to cache breeds in Room", e)
            }
        }

        return firebaseResult
    }

    private suspend fun fetchBreedListFromFirebase(currentPage: Int): MutableList<Dog> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_BREEDS)
                .orderByKey()
                .startAt((currentPage * TOTAL_ITEM_EACH_LOAD).toString())
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val dogList = mutableListOf<Dog>()
                        if (dataSnapshot.hasChildren()) {
                            for (snapshot in dataSnapshot.children) {
                                dogList.add(snapshot.getValue(Dog::class.java)!!)
                            }
                        }
                        continuation.resume(dogList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("DataBaseBaseSourceImpl", "Failed to read value.", error.toException())
                        continuation.resume(mutableListOf())
                        FirebaseCrashlytics.getInstance().recordException(error.toException())
                    }
                })
        }
    }

    override suspend fun getRandomBreedsWithWeight(count: Int): List<Dog> {
        val cached = dogDao.getRandomBreedsWithWeight(count).map { it.toDomain() }
        if (cached.size >= count) return cached

        val fallback = getRandomBreedsFromFirebase(count) { dog ->
            dog.maxWeightKg > 0.0 || dog.maxHeightCm > 0.0
        }

        return (cached + fallback)
            .distinctBy { it.name }
            .take(count)
    }

    override suspend fun getRandomBreedsWithDescription(count: Int): List<Dog> {
        val cached = dogDao.getRandomBreedsWithDescription(count).map { it.toDomain() }
        if (cached.size >= count) return cached

        val fallback = getRandomBreedsFromFirebase(count) { dog ->
            dog.temperament.isNotBlank() ||
                dog.description.isNotBlank() ||
                dog.origin.isNotBlank() ||
                dog.breedGroup.isNotBlank()
        }

        return (cached + fallback)
            .distinctBy { it.name }
            .take(count)
    }

    private suspend fun getRandomBreedsFromFirebase(
        count: Int,
        predicate: (Dog) -> Boolean
    ): List<Dog> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_BREEDS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val dogs = dataSnapshot.children
                            .mapNotNull { snapshot -> snapshot.getValue(Dog::class.java) }
                            .filter(predicate)
                            .shuffled()
                            .take(count)

                        continuation.resume(dogs)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("DataBaseSourceImpl", "Failed to read random breeds from firebase.", error.toException())
                        continuation.resume(emptyList())
                        FirebaseCrashlytics.getInstance().recordException(error.toException())
                    }
                })
        }
    }

    override suspend fun getAppsRecommended(): MutableList<App> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_APPS)
                .addValueEventListener(object : ValueEventListener {

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
                            continuation.resumeWith(Result.failure(e))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("DataBaseBaseSourceImpl", "Failed to read value.", error.toException())
                        continuation.resume(mutableListOf())
                        FirebaseCrashlytics.getInstance().recordException(error.toException())
                    }
                })
        }
    }
}
