package com.alvaroquintana.adivinaperro.datasource

import com.alvaroquintana.adivinaperro.utils.Constants.COLLECTION_RANKING
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.data.datasource.FirestoreDataSource
import com.alvaroquintana.data.repository.RepositoryException
import com.alvaroquintana.domain.User
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirestoreDataSourceImpl(private val database: FirebaseFirestore) : FirestoreDataSource {

    override suspend fun addRecord(user: User): Result<User> {
        return suspendCancellableCoroutine { continuation ->
            database.collection(COLLECTION_RANKING)
                .add(user)
                .addOnSuccessListener {
                    continuation.resume(Result.success(user))
                }
                .addOnFailureListener {
                    continuation.resume(Result.failure(RepositoryException.NoConnectionException))
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
        }
    }

    override suspend fun getRanking(): MutableList<User> {
        return suspendCancellableCoroutine { continuation ->
            val ref = database
                .collection(COLLECTION_RANKING)
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(30)

            ref.get()
                .addOnSuccessListener {
                    continuation.resume(it.toObjects<User>().toMutableList())
                }
                .addOnFailureListener {
                    continuation.resume(mutableListOf())
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
        }
    }

    override suspend fun getWorldRecords(limit: Long): String {
        return suspendCancellableCoroutine { continuation ->
            val ref = database
                .collection(COLLECTION_RANKING)
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(limit)

            ref.get()
                .addOnSuccessListener {
                    continuation.resume(it.toObjects<User>().last().points.toString())
                }
                .addOnFailureListener {
                    continuation.resume("")
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
        }
    }
}