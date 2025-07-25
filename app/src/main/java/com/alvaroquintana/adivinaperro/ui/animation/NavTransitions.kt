package com.alvaroquintana.adivinaperro.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

object NavTransitions {

    val enterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth / 4 },
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    val exitTransition: ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        )
    )

    val popEnterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    val popExitTransition: ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 4 },
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        )
    )

    // Vertical slide + scale for result/game-over screen
    val resultEnterTransition: EnterTransition = scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(350, easing = FastOutSlowInEasing)
    ) + fadeIn(tween(300))

    val resultExitTransition: ExitTransition = scaleOut(
        targetScale = 0.92f,
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(tween(200))

    // Fade only for settings/info
    val fadeEnterTransition: EnterTransition = fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val fadeExitTransition: ExitTransition = fadeOut(
        animationSpec = tween(200, easing = FastOutSlowInEasing)
    )
}
