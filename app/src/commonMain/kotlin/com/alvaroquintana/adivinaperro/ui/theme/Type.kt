package com.alvaroquintana.adivinaperro.ui.theme

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.dynapuff_bold
import adivinaraza.app.generated.resources.dynapuff_condensed_bold
import adivinaraza.app.generated.resources.dynapuff_condensed_medium
import adivinaraza.app.generated.resources.dynapuff_condensed_regular
import adivinaraza.app.generated.resources.dynapuff_condensed_semibold
import adivinaraza.app.generated.resources.dynapuff_medium
import adivinaraza.app.generated.resources.dynapuff_regular
import adivinaraza.app.generated.resources.dynapuff_semibold
import adivinaraza.app.generated.resources.dynapuff_semicondensed_bold
import adivinaraza.app.generated.resources.dynapuff_semicondensed_medium
import adivinaraza.app.generated.resources.dynapuff_semicondensed_regular
import adivinaraza.app.generated.resources.dynapuff_semicondensed_semibold
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

@Composable
fun dynaPuffFamily(): FontFamily = FontFamily(
    Font(Res.font.dynapuff_regular, FontWeight.Light),
    Font(Res.font.dynapuff_regular, FontWeight.Normal),
    Font(Res.font.dynapuff_medium, FontWeight.Medium),
    Font(Res.font.dynapuff_semibold, FontWeight.SemiBold),
    Font(Res.font.dynapuff_bold, FontWeight.Bold)
)

@Composable
fun dynaPuffSemiCondensedFamily(): FontFamily = FontFamily(
    Font(Res.font.dynapuff_semicondensed_regular, FontWeight.Light),
    Font(Res.font.dynapuff_semicondensed_regular, FontWeight.Normal),
    Font(Res.font.dynapuff_semicondensed_medium, FontWeight.Medium),
    Font(Res.font.dynapuff_semicondensed_semibold, FontWeight.SemiBold),
    Font(Res.font.dynapuff_semicondensed_bold, FontWeight.Bold)
)

@Composable
fun dynaPuffCondensedFamily(): FontFamily = FontFamily(
    Font(Res.font.dynapuff_condensed_regular, FontWeight.Light),
    Font(Res.font.dynapuff_condensed_regular, FontWeight.Normal),
    Font(Res.font.dynapuff_condensed_medium, FontWeight.Medium),
    Font(Res.font.dynapuff_condensed_semibold, FontWeight.SemiBold),
    Font(Res.font.dynapuff_condensed_bold, FontWeight.Bold)
)

@Composable
fun adivinaTypography(): Typography {
    val family = dynaPuffFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp,
            lineHeight = 52.sp
        ),
        displaySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}
