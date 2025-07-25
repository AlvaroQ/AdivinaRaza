package com.alvaroquintana.adivinaperro.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.theme.GameDark
import com.alvaroquintana.adivinaperro.ui.theme.GameMuted
import com.alvaroquintana.adivinaperro.ui.theme.GameOrange
import com.alvaroquintana.adivinaperro.ui.theme.JetBrainsMonoFamily
import com.alvaroquintana.adivinaperro.ui.theme.GeistFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToSelect: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "splash_alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splash_rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "paw_rotation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onNavigateToSelect()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            // Dog circular image
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                BreedImage(
                    imageData = "https://firebasestorage.googleapis.com/v0/b/adivinaperro.appspot.com/o/splash_dog.png?alt=media",
                    contentDescription = "Splash dog",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = GameDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Paw icon spinning
            Icon(
                painter = painterResource(R.drawable.ic_paw),
                contentDescription = null,
                tint = GameOrange,
                modifier = Modifier
                    .size(36.dp)
                    .rotate(rotation)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading text
            Text(
                text = "Cargando...",
                fontFamily = GeistFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = GameMuted
            )
        }
    }
}
