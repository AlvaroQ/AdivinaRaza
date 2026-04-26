package com.alvaroquintana.adivinaperro

import androidx.compose.runtime.Composable
import platform.Foundation.NSBundle

actual fun appVersionName(): String =
    NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: ""

actual fun appVersionCode(): Int =
    (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)?.toIntOrNull() ?: 0

@Composable
actual fun HandleBackPress(enabled: Boolean, onBack: () -> Unit) {
    // iOS uses native swipe-back via UINavigationController; no-op here.
}
