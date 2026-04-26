@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.alvaroquintana.data.datasource

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.ios
import platform.Foundation.NSDate
import platform.Foundation.NSNull
import platform.Foundation.NSNumber
import platform.Foundation.timeIntervalSince1970

actual fun DocumentSnapshot.rawData(): Map<String, Any?> {
    val native = ios.data() ?: return emptyMap()
    val converted = mutableMapOf<String, Any?>()
    for (entry in native) {
        val key = entry.key as? String ?: continue
        converted[key] = entry.value.toKotlinValue()
    }
    return converted
}

private fun Any?.toKotlinValue(): Any? = when (val v = this) {
    null -> null
    is NSNull -> null
    is String, is Boolean, is Int, is Long, is Double, is Float -> v
    is NSNumber -> v.toKotlinNumber()
    is NSDate -> (v.timeIntervalSince1970 * 1000.0).toLong()
    is Map<*, *> -> {
        val out = mutableMapOf<String, Any?>()
        for ((k, item) in v) {
            val ks = k as? String ?: continue
            out[ks] = item.toKotlinValue()
        }
        out
    }
    is List<*> -> v.map { it.toKotlinValue() }
    else -> v.toString()
}

private fun NSNumber.toKotlinNumber(): Any {
    val d = this.doubleValue
    val asLong = d.toLong()
    return if (asLong.toDouble() == d) asLong else d
}
