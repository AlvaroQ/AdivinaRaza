package com.alvaroquintana.adivinaperro.ui.game

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.choose_one
import adivinaraza.app.generated.resources.mode_description
import adivinaraza.app.generated.resources.stage_value

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.ui.components.AnswerOptionCard
import com.alvaroquintana.adivinaperro.ui.components.AnswerState
import com.alvaroquintana.adivinaperro.ui.components.GameStatusRow
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.components.OptionGrid
import com.alvaroquintana.adivinaperro.ui.animation.AnimationSpecs
import com.alvaroquintana.adivinaperro.ui.theme.getBackgroundGradient
import com.alvaroquintana.adivinaperro.utils.playFailSound
import com.alvaroquintana.adivinaperro.utils.playSuccessSound
import kotlinx.coroutines.delay

@Composable
fun DescriptionScreenContent(
    viewModel: DescriptionViewModel,
    context: Context
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingState()
        return
    }

    var buttonsEnabled by remember(state.roundId) { mutableStateOf(true) }
    val answerStates = remember(state.roundId, state.options.size) {
        mutableStateListOf<AnswerState>().apply {
            repeat(state.options.size) { add(AnswerState.NEUTRAL) }
        }
    }

    LaunchedEffect(state.lastResult) {
        state.lastResult?.let { result ->
            when (result) {
                DescriptionViewModel.AnswerResult.CORRECT -> playSuccessSound(context)
                DescriptionViewModel.AnswerResult.INCORRECT -> playFailSound(context)
            }
            delay(AnimationSpecs.ANSWER_HOLD_DURATION.toLong())
            viewModel.proceedAfterResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundGradient())
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        GameStatusRow(
            stageLabel = stringResource(Res.string.stage_value, state.stage),
            lives = state.lives,
            score = state.score
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.mode_description),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(Res.string.choose_one),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            )
        ) {
            Text(
                text = state.descriptionText,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                lineHeight = 26.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        OptionGrid(options = state.options, modifier = Modifier.fillMaxWidth()) { index, option, cardModifier ->
            AnswerOptionCard(
                text = option,
                state = answerStates[index],
                enabled = buttonsEnabled,
                modifier = cardModifier,
                onClick = {
                    if (buttonsEnabled) {
                        buttonsEnabled = false
                        applyAnswerFeedbackStates(
                            answerStates = answerStates,
                            options = state.options,
                            correctAnswer = state.correctName,
                            selectedIndex = index
                        )
                        viewModel.onOptionSelected(option)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

