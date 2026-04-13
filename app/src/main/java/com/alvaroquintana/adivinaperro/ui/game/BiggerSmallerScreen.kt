package com.alvaroquintana.adivinaperro.ui.game

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.components.BreedCard
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.utils.playFailSound
import com.alvaroquintana.adivinaperro.utils.playSuccessSound
import kotlinx.coroutines.delay

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

    var answered by remember(state.stage) { mutableStateOf(false) }

    LaunchedEffect(state.lastResult, state.stage) {
        if (state.lastResult != null) {
            when (state.lastResult) {
                BiggerSmallerViewModel.AnswerResult.CORRECT -> playSuccessSound(context)
                BiggerSmallerViewModel.AnswerResult.INCORRECT -> playFailSound(context)
                null -> {}
            }
            delay(1200)
            viewModel.proceedAfterResult()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(GameCream).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val questionText = when (state.comparisonType) {
            BiggerSmallerViewModel.ComparisonType.WEIGHT -> "Which weighs more?"
            BiggerSmallerViewModel.ComparisonType.HEIGHT -> "Which is taller?"
        }

        Text(
            text = questionText,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.breedLeft?.let { breed ->
                BreedCard(
                    breed = breed,
                    modifier = Modifier.weight(1f),
                    enabled = !answered,
                    onClick = {
                        answered = true
                        viewModel.onBreedSelected(isLeftSelected = true)
                    }
                )
            }

            state.breedRight?.let { breed ->
                BreedCard(
                    breed = breed,
                    modifier = Modifier.weight(1f),
                    enabled = !answered,
                    onClick = {
                        answered = true
                        viewModel.onBreedSelected(isLeftSelected = false)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Score: ${state.score}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}
