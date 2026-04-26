package com.alvaroquintana.adivinaperro.ui.components

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.ic_paw

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class PawPrint(
    val xFraction: Float,  // 0-1 position across screen width
    val yFraction: Float,  // 0-1 position across screen height
    val size: Float,        // sp size
    val baseAlpha: Float,   // 0.05 - 0.15
    val floatDuration: Int, // 2000-4000ms
    val floatAmplitude: Float, // 8-16dp vertical movement
    val rotationRange: Float,  // ±5-10 degrees
    val phaseOffset: Int    // stagger start
)

@Composable
fun FloatingPawPrints(
    modifier: Modifier = Modifier,
    count: Int = 4
) {
    val pawPrints = remember {
        List(count) {
            PawPrint(
                xFraction = Random.nextFloat(),
                yFraction = Random.nextFloat(),
                size = Random.nextFloat() * 12f + 18f, // 18-30sp
                baseAlpha = Random.nextFloat() * 0.07f + 0.03f, // 0.03-0.10
                floatDuration = Random.nextInt(2000, 4001),
                floatAmplitude = Random.nextFloat() * 8f + 8f, // 8-16dp
                rotationRange = Random.nextFloat() * 5f + 5f, // 5-10 degrees
                phaseOffset = Random.nextInt(0, 2000)
            )
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)

    Box(modifier = modifier.fillMaxSize()) {
        pawPrints.forEach { paw ->
            val infiniteTransition = rememberInfiniteTransition(label = "paw_float")

            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -paw.floatAmplitude,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = paw.floatDuration,
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(paw.phaseOffset)
                ),
                label = "y"
            )

            val rotation by infiniteTransition.animateFloat(
                initialValue = -paw.rotationRange,
                targetValue = paw.rotationRange,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (paw.floatDuration * 1.3f).toInt(),
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(paw.phaseOffset + 500)
                ),
                label = "rot"
            )

            val alpha by infiniteTransition.animateFloat(
                initialValue = paw.baseAlpha,
                targetValue = paw.baseAlpha * 2.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (paw.floatDuration * 0.8f).toInt(),
                        easing = EaseInOutSine
                    ),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(paw.phaseOffset + 300)
                ),
                label = "alpha"
            )

            Icon(
                painter = painterResource(Res.drawable.ic_paw),
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(paw.size.dp)
                    .offset(
                        x = (screenWidth * paw.xFraction).dp,
                        y = (screenHeight * paw.yFraction).dp + offsetY.dp
                    )
                    .rotate(rotation)
                    .alpha(alpha)
            )
        }
    }
}
