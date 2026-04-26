@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.alvaroquintana.adivinaperro.ui.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@Composable
actual fun rememberAppWindowSizeClass(): AppWindowSizeClass {
    val activity = LocalActivity.current ?: throw IllegalStateException("No Activity found")
    val windowSizeClass = calculateWindowSizeClass(activity)
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Medium -> AppWindowSizeClass.Medium
        WindowWidthSizeClass.Expanded -> AppWindowSizeClass.Expanded
        else -> AppWindowSizeClass.Compact
    }
}
