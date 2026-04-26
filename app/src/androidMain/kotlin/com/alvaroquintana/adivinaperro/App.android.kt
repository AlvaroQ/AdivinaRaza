package com.alvaroquintana.adivinaperro

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

actual fun appVersionName(): String = BuildConfig.VERSION_NAME
actual fun appVersionCode(): Int = BuildConfig.VERSION_CODE

@Composable
actual fun HandleBackPress(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled) { onBack() }
}
