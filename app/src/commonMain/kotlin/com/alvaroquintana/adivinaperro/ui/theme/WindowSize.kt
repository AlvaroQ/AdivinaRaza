package com.alvaroquintana.adivinaperro.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppWindowSizeClass { Compact, Medium, Expanded }

val LocalWindowSizeClass = staticCompositionLocalOf { AppWindowSizeClass.Compact }

@Composable
expect fun rememberAppWindowSizeClass(): AppWindowSizeClass

val AppWindowSizeClass.isCompact: Boolean
    get() = this == AppWindowSizeClass.Compact

val AppWindowSizeClass.isMedium: Boolean
    get() = this == AppWindowSizeClass.Medium

val AppWindowSizeClass.isExpanded: Boolean
    get() = this == AppWindowSizeClass.Expanded
