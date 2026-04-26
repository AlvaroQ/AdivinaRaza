package com.alvaroquintana.data.datasource

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.android

actual fun DocumentSnapshot.rawData(): Map<String, Any?> {
    @Suppress("UNCHECKED_CAST")
    return android.data as? Map<String, Any?> ?: emptyMap()
}
