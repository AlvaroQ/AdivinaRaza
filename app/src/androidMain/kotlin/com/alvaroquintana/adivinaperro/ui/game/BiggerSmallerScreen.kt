package com.alvaroquintana.adivinaperro.ui.game

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.choose_one
import adivinaraza.app.generated.resources.question_height_taller
import adivinaraza.app.generated.resources.question_weight_more
import adivinaraza.app.generated.resources.stage_value

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.animation.AnimationSpecs
import com.alvaroquintana.adivinaperro.ui.components.BreedCardFeedbackState
import com.alvaroquintana.adivinaperro.ui.components.BreedCard
import com.alvaroquintana.adivinaperro.ui.components.GameStatusRow
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.theme.getBackgroundGradient
import com.alvaroquintana.adivinaperro.utils.playFailSound
import com.alvaroquintana.adivinaperro.utils.playSuccessSound
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun BiggerSmallerScreenContent(
    viewModel: BiggerSmallerViewModel,
    context: Context
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingState()
        return
    }

    var answered by remember(state.roundId) { mutableStateOf(false) }
    var selectedLeft by remember(state.roundId) { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(state.lastResult) {
        if (state.lastResult != null) {
            when (state.lastResult) {
                BiggerSmallerViewModel.AnswerResult.CORRECT -> playSuccessSound(context)
                BiggerSmallerViewModel.AnswerResult.INCORRECT -> playFailSound(context)
                else -> Unit
            }
            delay(AnimationSpecs.ANSWER_HOLD_DURATION.toLong())
            viewModel.proceedAfterResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundGradient())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        GameStatusRow(
            stageLabel = stringResource(Res.string.stage_value, state.stage),
            lives = state.lives,
            score = state.score
        )

        Spacer(modifier = Modifier.height(16.dp))

        val questionText = when (state.comparisonType) {
            BiggerSmallerViewModel.ComparisonType.WEIGHT -> stringResource(Res.string.question_weight_more)
            BiggerSmallerViewModel.ComparisonType.HEIGHT -> stringResource(Res.string.question_height_taller)
        }

        Text(
            text = questionText,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.choose_one),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        val leftState = resolveBreedCardState(
            isLeftCard = true,
            selectedLeft = selectedLeft,
            lastResult = state.lastResult,
            leftValue = when (state.comparisonType) {
                BiggerSmallerViewModel.ComparisonType.WEIGHT -> state.breedLeft?.maxWeightKg
                BiggerSmallerViewModel.ComparisonType.HEIGHT -> state.breedLeft?.maxHeightCm
            },
            rightValue = when (state.comparisonType) {
                BiggerSmallerViewModel.ComparisonType.WEIGHT -> state.breedRight?.maxWeightKg
                BiggerSmallerViewModel.ComparisonType.HEIGHT -> state.breedRight?.maxHeightCm
            }
        )
        val rightState = resolveBreedCardState(
            isLeftCard = false,
            selectedLeft = selectedLeft,
            lastResult = state.lastResult,
            leftValue = when (state.comparisonType) {
                BiggerSmallerViewModel.ComparisonType.WEIGHT -> state.breedLeft?.maxWeightKg
                BiggerSmallerViewModel.ComparisonType.HEIGHT -> state.breedLeft?.maxHeightCm
            },
            rightValue = when (state.comparisonType) {
                BiggerSmallerViewModel.ComparisonType.WEIGHT -> state.breedRight?.maxWeightKg
                BiggerSmallerViewModel.ComparisonType.HEIGHT -> state.breedRight?.maxHeightCm
            }
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.breedLeft?.let { breed ->
                    BreedCard(
                        breed = breed,
                        modifier = Modifier.weight(1f),
                        enabled = !answered,
                        feedbackState = leftState,
                        onClick = {
                            answered = true
                            selectedLeft = true
                            viewModel.onBreedSelected(isLeftSelected = true)
                        }
                    )
                }

                state.breedRight?.let { breed ->
                    BreedCard(
                        breed = breed,
                        modifier = Modifier.weight(1f),
                        enabled = !answered,
                        feedbackState = rightState,
                        onClick = {
                            answered = true
                            selectedLeft = false
                            viewModel.onBreedSelected(isLeftSelected = false)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

private fun resolveBreedCardState(
    isLeftCard: Boolean,
    selectedLeft: Boolean?,
    lastResult: BiggerSmallerViewModel.AnswerResult?,
    leftValue: Double?,
    rightValue: Double?
): BreedCardFeedbackState {
    if (lastResult == null || selectedLeft == null || leftValue == null || rightValue == null) {
        return BreedCardFeedbackState.NEUTRAL
    }

    val tolerance = maxOf(leftValue, rightValue) * 0.10
    val isTie = kotlin.math.abs(leftValue - rightValue) <= tolerance
    val leftIsCorrect = isTie || leftValue >= rightValue

    if (isTie && lastResult == BiggerSmallerViewModel.AnswerResult.CORRECT) {
        return BreedCardFeedbackState.CORRECT
    }

    val selectedIsThisCard = selectedLeft == isLeftCard
    return when {
        selectedIsThisCard && lastResult == BiggerSmallerViewModel.AnswerResult.CORRECT -> BreedCardFeedbackState.CORRECT
        selectedIsThisCard && lastResult == BiggerSmallerViewModel.AnswerResult.INCORRECT -> BreedCardFeedbackState.WRONG
        !selectedIsThisCard && isLeftCard == leftIsCorrect -> BreedCardFeedbackState.CORRECT
        else -> BreedCardFeedbackState.NEUTRAL
    }
}
