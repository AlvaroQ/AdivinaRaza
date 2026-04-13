package com.alvaroquintana.adivinaperro.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.IntOffset

/**
 * Shared animation specifications for the AdivinaPerro design system.
 */
object AnimationSpecs {

    // --- Durations ---
    const val ANSWER_COLOR_DURATION = 300
    const val ANSWER_HOLD_DURATION = 1300
    const val QUESTION_SLIDE_DURATION = 400
    const val WRONG_SHAKE_DURATION = 400
    const val SCORE_COUNT_DURATION = 600
    const val POINTS_POPUP_DURATION = 800
    const val NAV_TRANSITION_DURATION = 350
    const val NAV_FADE_DURATION = 300
    const val STREAK_PULSE_DURATION = 600
    const val LOADING_BOUNCE_DURATION = 600
    const val LOADING_STAGGER_DELAY = 150
    const val IDLE_FLOAT_DURATION_MIN = 2000
    const val IDLE_FLOAT_DURATION_MAX = 4000
    const val REDUCED_MOTION_DURATION = 0
    const val CARD_PRESS_DURATION = 100
    const val HERO_FADE_DURATION = 500
    const val STAGGER_DELAY = 50
    const val CONFETTI_DURATION = 2000

    // --- Easing ---
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    // --- Springs ---
    val CorrectBounceSpec: SpringSpec<Float> = spring(
        dampingRatio = 0.4f,
        stiffness = Spring.StiffnessMedium
    )

    val StreakAppearSpec: SpringSpec<Float> = spring(
        dampingRatio = 0.5f,
        stiffness = Spring.StiffnessMediumLow
    )

    // --- Tweens ---
    val AnswerColorSpec: TweenSpec<androidx.compose.ui.graphics.Color> = tween(
        durationMillis = ANSWER_COLOR_DURATION,
        easing = FastOutSlowInEasing
    )

    val QuestionSlideSpec: FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = QUESTION_SLIDE_DURATION,
        easing = FastOutSlowInEasing
    )

    val QuestionFadeSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = QUESTION_SLIDE_DURATION - 100,
        delayMillis = 100,
        easing = FastOutSlowInEasing
    )

    val ScoreCountSpec: TweenSpec<Float> = tween(
        durationMillis = SCORE_COUNT_DURATION,
        easing = FastOutSlowInEasing
    )

    val NavEnterSpec: FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = NAV_TRANSITION_DURATION,
        easing = FastOutSlowInEasing
    )

    val NavExitSpec: FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = NAV_TRANSITION_DURATION,
        easing = FastOutSlowInEasing
    )

    val NavFadeInSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = NAV_FADE_DURATION,
        delayMillis = 100,
        easing = FastOutSlowInEasing
    )

    val NavFadeOutSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = NAV_FADE_DURATION - 50,
        easing = FastOutSlowInEasing
    )

    // --- Shake Keyframes ---
    fun wrongShakeSpec(): KeyframesSpec<Float> = keyframes {
        durationMillis = WRONG_SHAKE_DURATION
        0f at 0 using LinearEasing
        -8f at 50 using FastOutSlowInEasing
        8f at 100 using FastOutSlowInEasing
        -6f at 175 using FastOutSlowInEasing
        6f at 250 using FastOutSlowInEasing
        -3f at 325 using FastOutSlowInEasing
        0f at WRONG_SHAKE_DURATION using FastOutSlowInEasing
    }

    // --- Points Popup ---
    val PointsPopupOffsetSpec: TweenSpec<Float> = tween(
        durationMillis = POINTS_POPUP_DURATION,
        easing = LinearOutSlowInEasing
    )

    val PointsPopupFadeSpec: TweenSpec<Float> = tween(
        durationMillis = POINTS_POPUP_DURATION,
        delayMillis = 200,
        easing = LinearEasing
    )
}
