package com.alvaroquintana.adivinaperro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Replica exactamente la lógica de AdivinaPerroTheme para saber si estamos en modo oscuro.
 * Usa LocalThemeMode (preferencia de la app) en lugar de isSystemInDarkTheme() directamente,
 * de modo que la elección de tema de la app siempre se respeta.
 */
@Composable
@ReadOnlyComposable
fun isAppInDarkTheme(): Boolean {
    val themeMode = LocalThemeMode.current
    return when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
    }
}

// ── Light theme gradients ────────────────────────────────────────────────────
object LightGradients {
    val backgroundGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                GameCream,
                Color(0xFFFFF4D9)
            )
        )

    val cardGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFFFFAF0)
            )
        )

    val surfaceGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF5F0E0),
                Color(0xFFEBE4D2)
            )
        )

    val orangeGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(
                GameOrange,
                Color(0xFFFFB366)
            )
        )

    val heroGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                GameCream,
                Color(0xFFFFE8D0)
            )
        )
}

// ── Dark theme gradients ─────────────────────────────────────────────────────
// De negro cálido (top) a madera oscura con un toque de calor (bottom)
object DarkGradients {
    val backgroundGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                DarkBackground,          // #1C1208 — casi negro cálido
                Color(0xFF241608)        // ligeramente más cálido abajo
            )
        )

    val cardGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(
                DarkSurface,             // #261A0E
                Color(0xFF1E1409)        // borde inferior más oscuro
            )
        )

    val surfaceGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                DarkSurfaceVar,          // #352515
                Color(0xFF2C1E10)        // más profundo abajo
            )
        )

    val orangeGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(
                GameOrange,
                Color(0xFFE68033)
            )
        )

    // Hero: de casi negro (top) a madera oscura con brillo naranja suave (bottom)
    val heroGradient: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF120C06),       // negro cálido — parte superior
                Color(0xFF2A1C10)        // madera oscura — sugiere calor abajo
            )
        )
}

@Composable
fun getBackgroundGradient(): Brush =
    if (isAppInDarkTheme()) DarkGradients.backgroundGradient else LightGradients.backgroundGradient

@Composable
fun getCardGradient(): Brush =
    if (isAppInDarkTheme()) DarkGradients.cardGradient else LightGradients.cardGradient

@Composable
fun getSurfaceGradient(): Brush =
    if (isAppInDarkTheme()) DarkGradients.surfaceGradient else LightGradients.surfaceGradient

@Composable
fun getOrangeGradient(): Brush =
    if (isAppInDarkTheme()) DarkGradients.orangeGradient else LightGradients.orangeGradient

@Composable
fun getHeroGradient(): Brush =
    if (isAppInDarkTheme()) DarkGradients.heroGradient else LightGradients.heroGradient




