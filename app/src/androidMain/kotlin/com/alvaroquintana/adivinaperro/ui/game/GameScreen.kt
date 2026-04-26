package com.alvaroquintana.adivinaperro.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alvaroquintana.adivinaperro.ui.components.AnswerOptionCard
import com.alvaroquintana.adivinaperro.ui.components.AnswerState
import com.alvaroquintana.adivinaperro.ui.components.GameStatusRow
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.components.OptionGrid
import com.alvaroquintana.adivinaperro.ui.components.QuestionCard
import androidx.compose.material3.MaterialTheme
import com.alvaroquintana.adivinaperro.ui.theme.dynaPuffFamily
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.theme.getBackgroundGradient

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    stage: Int,
    lives: Int = 3,
    points: Int = 0,
    onAnswerSelected: (selectedIndex: Int, correctName: String, options: List<String>) -> Unit
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val questionImage by viewModel.question.collectAsStateWithLifecycle()

    val isLoading = progress is GameViewModel.UiModel.Loading &&
            (progress as GameViewModel.UiModel.Loading).show

    var options by remember { mutableStateOf(listOf("", "", "", "")) }
    var buttonsVisible by remember { mutableStateOf(false) }
    var buttonsEnabled by remember { mutableStateOf(true) }
    val answerStates = remember { mutableStateListOf(AnswerState.NEUTRAL, AnswerState.NEUTRAL, AnswerState.NEUTRAL, AnswerState.NEUTRAL) }

    LaunchedEffect(Unit) {
        viewModel.responseOptions.collect { optionList ->
            options = optionList.toList()
            for (i in answerStates.indices) {
                answerStates[i] = AnswerState.NEUTRAL
            }
            buttonsEnabled = true

            if (stage == 1) {
                buttonsVisible = true
            } else {
                buttonsVisible = false
                delay(50)
                buttonsVisible = true
            }
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            buttonsEnabled = false
            for (i in answerStates.indices) {
                answerStates[i] = AnswerState.NEUTRAL
            }
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
            stageLabel = "$stage/$TOTAL_BREED",
            lives = lives,
            score = points
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Question image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LoadingState()
            }

            if (questionImage.isNotEmpty() && !isLoading) {
                QuestionCard(
                    imageUrl = questionImage,
                    questionNumber = stage,
                    totalQuestions = TOTAL_BREED,
                    modifier = Modifier.fillMaxSize(),
                    imageContentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question text
        Text(
            text = stringResource(R.string.question_guess_breed),
            fontFamily = dynaPuffFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Answer options
        AnimatedVisibility(
            visible = buttonsVisible && !isLoading,
            enter = if (stage <= 1) {
                fadeIn(animationSpec = tween(100))
            } else {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(200)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionGrid(options = options, modifier = Modifier.fillMaxWidth()) { index, option, cardModifier ->
                AnswerOptionCard(
                    text = option,
                    state = answerStates[index],
                    enabled = buttonsEnabled,
                    modifier = cardModifier,
                    onClick = {
                        if (buttonsEnabled) {
                            buttonsEnabled = false
                            val correctName: String = viewModel.getNameBreedCorrect()
                            applyAnswerFeedbackStates(answerStates, options, correctName, index)
                            onAnswerSelected(index, correctName, options)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

