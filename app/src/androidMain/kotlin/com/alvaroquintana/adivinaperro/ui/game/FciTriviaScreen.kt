package com.alvaroquintana.adivinaperro.ui.game

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.animation.AnimationSpecs
import com.alvaroquintana.adivinaperro.ui.components.AnswerOptionCard
import com.alvaroquintana.adivinaperro.ui.components.AnswerState
import com.alvaroquintana.adivinaperro.ui.components.GameStatusRow
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.components.OptionGrid
import com.alvaroquintana.adivinaperro.ui.components.QuestionCard
import com.alvaroquintana.adivinaperro.ui.theme.dynaPuffFamily
import com.alvaroquintana.adivinaperro.ui.theme.getBackgroundGradient
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.adivinaperro.utils.playFailSound
import com.alvaroquintana.adivinaperro.utils.playSuccessSound
import kotlinx.coroutines.delay

@Composable
fun FciTriviaScreenContent(
    viewModel: FciTriviaViewModel,
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
                FciTriviaViewModel.AnswerResult.CORRECT -> playSuccessSound(context)
                FciTriviaViewModel.AnswerResult.INCORRECT -> playFailSound(context)
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
            stageLabel = stringResource(R.string.stage_value, state.stage),
            lives = state.lives,
            score = state.score
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.mode_fci_trivia),
            fontFamily = dynaPuffFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        QuestionCard(
            imageUrl = state.questionImage,
            questionNumber = state.stage,
            totalQuestions = TOTAL_BREED,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            imageContentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.question_fci_group),
            fontFamily = dynaPuffFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OptionGrid(
            options = state.options,
            modifier = Modifier.fillMaxWidth(),
            optionContent = { index, option, cardModifier ->
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
                                correctAnswer = state.correctAnswer,
                                selectedIndex = index
                            )
                            viewModel.onOptionSelected(option)
                        }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
