package com.alvaroquintana.adivinaperro.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun ConfigureSystemBars(darkTheme: Boolean, backgroundColor: Color) {
    // Status bar appearance on iOS is configured via Info.plist (UIStatusBarStyle)
    // and per-screen UIViewController overrides; phase 6 wires that up.
}
