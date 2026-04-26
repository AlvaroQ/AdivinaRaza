package com.alvaroquintana.adivinaperro.managers

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual object ErrorTracker {
    private val crashlytics get() = FirebaseCrashlytics.getInstance()

    actual fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    actual fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}
