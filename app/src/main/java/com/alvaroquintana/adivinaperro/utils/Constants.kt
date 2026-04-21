package com.alvaroquintana.adivinaperro.utils

object Constants {
    const val RECORD_PERSONAL = "personalRecord"
    const val POINTS = "points"
    const val TOTAL_BREED = 322
    const val TOTAL_ITEM_EACH_LOAD = 15
    const val PATH_REFERENCE_BREEDS = "dog/breeds/"
    const val PATH_REFERENCE_APPS = "dog/apps"

    // Firestore collections
    const val COLLECTION_BREEDS_ES = "breedES"
    const val STALE_THRESHOLD_MS = 86_400_000L // 24 hours
    const val SYNC_COLLECTION_BREEDS = "breeds"
    const val SYNC_COLLECTION_BREEDS_ES = "breeds_es"

    // Game mode keys
    const val GAME_MODE_CLASSIC = "classic"
    const val GAME_MODE_BIGGER_SMALLER = "bigger_smaller"
    const val GAME_MODE_DESCRIPTION = "description"
}