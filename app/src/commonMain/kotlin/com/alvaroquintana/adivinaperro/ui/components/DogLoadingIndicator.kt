package com.alvaroquintana.adivinaperro.ui.components

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.ic_paw

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.animation.AnimationSpecs

@Composable
fun DogLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(3) { index ->
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = AnimationSpecs.LOADING_BOUNCE_DURATION
                        0f at 0
                        -12f at 200 using FastOutSlowInEasing
                        0f at 400 using FastOutSlowInEasing
                        0f at AnimationSpecs.LOADING_BOUNCE_DURATION
                    },
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(
                        index * AnimationSpecs.LOADING_STAGGER_DELAY
                    )
                ),
                label = "paw_$index"
            )

            Icon(
                painter = painterResource(Res.drawable.ic_paw),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .offset(y = offsetY.dp)
            )
        }
    }
}
