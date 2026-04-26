package com.alvaroquintana.adivinaperro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.theme.AdivinaPerroTheme
import com.alvaroquintana.adivinaperro.ui.theme.ThemeMode

/**
 * Multiplatform Compose entry point. Phase 6a wires this up as a thin
 * theme-only shell so the iOS Xcode project can link against the
 * AdivinaRazaShared framework via `MainViewController()` and verify the
 * Compose Multiplatform pipeline boots end-to-end. Phase 6b moves the
 * full navigation host (currently in MainActivity.kt) into this `App`
 * composable so both platforms share the same UI surface.
 */
@Composable
fun App(themeMode: ThemeMode = ThemeMode.SYSTEM) {
    AdivinaPerroTheme(themeMode = themeMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "AdivinaRaza",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Compose Multiplatform pipeline OK",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
