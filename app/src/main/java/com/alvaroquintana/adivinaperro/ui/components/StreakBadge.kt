package com.alvaroquintana.adivinaperro.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.animation.AnimationSpecs
import com.alvaroquintana.adivinaperro.ui.theme.LocalGameColors

@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val visible = streak >= 2

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = AnimationSpecs.StreakAppearSpec
        ) + fadeIn(),
        exit = scaleOut(tween(300)) + fadeOut(tween(300)),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = AnimationSpecs.STREAK_PULSE_DURATION,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )

        Surface(
            shape = RoundedCornerShape(50),
            color = gameColors.streakGoldContainer,
            tonalElevation = 2.dp,
            modifier = Modifier.scale(scale)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_fire),
                    contentDescription = "Streak",
                    tint = gameColors.streakGold,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${streak}x",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = gameColors.streakGold
                )
            }
        }
    }
}
