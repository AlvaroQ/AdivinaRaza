package com.alvaroquintana.adivinaperro.managers

actual object ErrorTracker {
    actual fun setCustomKey(key: String, value: String) = Unit
    actual fun setCustomKey(key: String, value: Int) = Unit
    actual fun recordException(throwable: Throwable) = Unit
}
