package com.alvaroquintana.adivinaperro.managers

expect object ErrorTracker {
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Int)
    fun recordException(throwable: Throwable)
}
