package com.alvaroquintana.adivinaperro.ui.game

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.components.AnswerOptionCard
import com.alvaroquintana.adivinaperro.ui.components.AnswerState
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.components.OptionGrid
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

    var buttonsEnabled by remember(state.stage) { mutableStateOf(true) }
    val answerStates = remember(state.stage, state.options) {
        mutableStateListOf<AnswerState>().apply {
            repeat(state.options.size) { add(AnswerState.NEUTRAL) }
        }
    }

    LaunchedEffect(state.lastResult, state.stage) {
        state.lastResult?.let { result ->
            when (result) {
                DescriptionViewModel.AnswerResult.CORRECT -> playSuccessSound(context)
                DescriptionViewModel.AnswerResult.INCORRECT -> playFailSound(context)
            }
            delay(1200)
            viewModel.proceedAfterResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GameCream)
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ),
                    shape = MaterialTheme.shapes.large
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Adivina la raza",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                lineHeight = 26.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OptionGrid(options = state.options, modifier = Modifier.fillMaxWidth()) { index, option, cardModifier ->
            AnswerOptionCard(
                text = option,
                state = answerStates[index],
                enabled = buttonsEnabled,
                modifier = cardModifier,
                onClick = {
                    if (buttonsEnabled) {
                        buttonsEnabled = false
                        markDescriptionAnswerStates(
                            answerStates = answerStates,
                            options = state.options,
                            correctName = state.correctName,
                            selectedIndex = index
                        )
                        viewModel.onOptionSelected(option)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Score: ${state.score}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

private fun markDescriptionAnswerStates(
    answerStates: MutableList<AnswerState>,
    options: List<String>,
    correctName: String,
    selectedIndex: Int
) {
    val correctIndex = options.indexOfFirst { it == correctName }
    if (correctIndex >= 0) {
        answerStates[correctIndex] = AnswerState.CORRECT
    }
    if (selectedIndex != correctIndex) {
        answerStates[selectedIndex] = AnswerState.WRONG
    }
}

