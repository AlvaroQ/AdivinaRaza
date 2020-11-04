package com.alvaroquintana.adivinaperro.datasource

import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.utils.Constants.PATH_REFERENCE_BREEDS
import com.alvaroquintana.adivinaperro.utils.Constants.PATH_REFERENCE_APPS
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
import com.google.firebase.database.ktx.getValue

class DataBaseSourceImpl : DataBaseSource {

    override suspend fun getBreedById(id: Int): Dog {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_BREEDS + id)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        continuation.resume(dataSnapshot.getValue(Dog::class.java) as Dog)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("getBreedById FAILED", "Failed to read value.", error.toException())
                        continuation.resume(Dog())
                    }
                })
        }
    }

    override suspend fun getAppsRecommended(): MutableList<App> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_APPS)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var value = dataSnapshot.getValue<MutableList<App>>()
                        if(value == null) value = mutableListOf()
                        continuation.resume(value.filter { it.url != BuildConfig.APPLICATION_ID }.toMutableList())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        log("DataBaseBaseSourceImpl", "Failed to read value.", error.toException())
                        continuation.resume(mutableListOf())
                    }
                })
        }
    }
}