package com.alvaroquintana.adivinaperro.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

object NavTransitions {
    private const val SHORT_FADE_IN_MS = 170
    private const val SHORT_FADE_OUT_MS = 130
    private const val SLIDE_MS = 300
    private const val SCALE_MS = 340
    private const val POP_SLIDE_MS = 360

    val enterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth / 5 },
        animationSpec = tween(
            durationMillis = SLIDE_MS,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = SHORT_FADE_IN_MS,
            easing = FastOutSlowInEasing
        )
    ) + scaleIn(
        initialScale = 0.97f,
        animationSpec = tween(
            durationMillis = SCALE_MS,
            easing = FastOutSlowInEasing
        )
    )

    val exitTransition: ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 6 },
        animationSpec = tween(
            durationMillis = SLIDE_MS,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = SHORT_FADE_OUT_MS,
            easing = FastOutSlowInEasing
        )
    ) + scaleOut(
        targetScale = 0.98f,
        animationSpec = tween(
            durationMillis = 260,
            easing = FastOutSlowInEasing
        )
    )

    val popEnterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 5 },
        animationSpec = tween(
            durationMillis = POP_SLIDE_MS,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = SLIDE_MS,
            easing = FastOutSlowInEasing
        )
    ) + scaleIn(
        initialScale = 0.97f,
        animationSpec = tween(
            durationMillis = SCALE_MS,
            easing = FastOutSlowInEasing
        )
    )

    val popExitTransition: ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 6 },
        animationSpec = tween(
            durationMillis = SLIDE_MS,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        )
    ) + scaleOut(
        targetScale = 0.98f,
        animationSpec = tween(
            durationMillis = 260,
            easing = FastOutSlowInEasing
        )
    )

    val resultEnterTransition: EnterTransition = scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(350, easing = FastOutSlowInEasing)
    ) + fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val resultExitTransition: ExitTransition = scaleOut(
        targetScale = 0.92f,
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(
        animationSpec = tween(200, easing = FastOutSlowInEasing)
    )

    val fadeEnterTransition: EnterTransition = fadeIn(
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val fadeExitTransition: ExitTransition = fadeOut(
        animationSpec = tween(200, easing = FastOutSlowInEasing)
    )
}
