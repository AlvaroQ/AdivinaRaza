package com.alvaroquintana.adivinaperro.datasource

import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.adivinaperro.utils.log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class DataBaseBaseSourceImpl : DataBaseSource {

    companion object {
        const val PATH_REFERENCE = "dog/breeds/"
    }

    override suspend fun getBreedById(id: Int): Dog {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE + id)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        log("Data Base SUCESS", "SUCCESS")
                        val value = dataSnapshot.getValue(Dog::class.java) as Dog
                        continuation.resume(value)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        log("Data Base FAILED", "Failed to read value.", error.toException())
                        continuation.resume(Dog())
                    }
                })
        }
    }
}