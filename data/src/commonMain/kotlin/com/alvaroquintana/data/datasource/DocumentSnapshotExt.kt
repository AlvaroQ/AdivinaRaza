package com.alvaroquintana.data.datasource

import dev.gitlive.firebase.firestore.DocumentSnapshot

/**
 * Platform-specific extraction of a Firestore document's raw payload.
 *
 * gitlive's `data(strategy)` deserializes through kotlinx.serialization,
 * which fails on iOS the moment a field type isn't representable as a
 * JsonElement (FIRTimestamp, FIRGeoPoint, NSDate, …). Going straight
 * to the native data dictionary sidesteps that entirely — we let
 * BreedEsMapper handle the dynamic Map shape it already accepts on
 * Android.
 */
expect fun DocumentSnapshot.rawData(): Map<String, Any?>
