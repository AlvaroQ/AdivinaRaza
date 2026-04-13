package com.alvaroquintana.adivinaperro.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.theme.GameDark
import com.alvaroquintana.adivinaperro.ui.theme.ComfortaaFamily

@Composable
fun GameAppBar(
    title: String,
    onBackClick: () -> Unit,
    showLives: Boolean = true,
    lives: Int = 3
) {
    Surface(
        color = GameCream,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = GameDark
                )
            }

            Text(
                text = title,
                color = GameDark,
                fontFamily = ComfortaaFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            if (showLives) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    LifeIcon(isAlive = lives >= 1)
                    LifeIcon(isAlive = lives >= 2)
                    LifeIcon(isAlive = lives >= 3)
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
private fun LifeIcon(isAlive: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isAlive) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "life_scale"
    )

    Image(
        painter = painterResource(
            id = if (isAlive) R.drawable.ic_life_on else R.drawable.ic_life_off
        ),
        contentDescription = "Life",
        modifier = Modifier
            .size(24.dp)
            .scale(scale)
    )
}
