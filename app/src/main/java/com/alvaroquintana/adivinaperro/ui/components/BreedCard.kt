package com.alvaroquintana.adivinaperro.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import com.alvaroquintana.adivinaperro.ui.animation.AnimationSpecs
import com.alvaroquintana.adivinaperro.ui.theme.LocalGameColors
import com.alvaroquintana.domain.Dog

enum class BreedCardFeedbackState { NEUTRAL, CORRECT, WRONG }

@Composable
fun BreedCard(
    breed: Dog,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    feedbackState: BreedCardFeedbackState = BreedCardFeedbackState.NEUTRAL,
    onClick: () -> Unit
) {
    val gameColors = LocalGameColors.current

    val containerTargetColor = when (feedbackState) {
        BreedCardFeedbackState.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
        BreedCardFeedbackState.CORRECT -> gameColors.correctContainer
        BreedCardFeedbackState.WRONG -> gameColors.wrongContainer
    }

    val contentTargetColor = when (feedbackState) {
        BreedCardFeedbackState.NEUTRAL -> MaterialTheme.colorScheme.onSurface
        BreedCardFeedbackState.CORRECT -> gameColors.onCorrectContainer
        BreedCardFeedbackState.WRONG -> gameColors.onWrongContainer
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            feedbackState == BreedCardFeedbackState.CORRECT -> gameColors.correctAnswer
            feedbackState == BreedCardFeedbackState.WRONG -> gameColors.wrongAnswer
            enabled -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = AnimationSpecs.AnswerColorSpec,
        label = "border"
    )

    val containerColor by animateColorAsState(
        targetValue = containerTargetColor,
        animationSpec = AnimationSpecs.AnswerColorSpec,
        label = "container"
    )

    val contentColor by animateColorAsState(
        targetValue = contentTargetColor,
        animationSpec = AnimationSpecs.AnswerColorSpec,
        label = "content"
    )

    val targetScale = when (feedbackState) {
        BreedCardFeedbackState.CORRECT -> 1.0f
        BreedCardFeedbackState.NEUTRAL,
        BreedCardFeedbackState.WRONG -> 1.0f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = when (feedbackState) {
            BreedCardFeedbackState.CORRECT -> AnimationSpecs.CorrectBounceSpec
            else -> spring(stiffness = Spring.StiffnessMedium)
        },
        label = "breedCardScale"
    )

    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(feedbackState) {
        if (feedbackState == BreedCardFeedbackState.WRONG) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = AnimationSpecs.wrongShakeSpec()
            )
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    Card(
        modifier = modifier
            .scale(animatedScale)
            .offset { IntOffset(shakeOffset.value.dp.roundToPx(), 0) }
            .clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Fondo blanco fijo para la imagen: elimina contraste con PNG de perro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                BreedImage(
                    imageData = breed.icon,
                    contentDescription = breed.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Área de texto con altura fija para garantizar simetría entre tarjetas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = breed.name,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 18.sp
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = feedbackState == BreedCardFeedbackState.CORRECT,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = gameColors.correctAnswer
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = feedbackState == BreedCardFeedbackState.WRONG,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = gameColors.wrongAnswer
                    )
                }
            }
        }
    }
}
