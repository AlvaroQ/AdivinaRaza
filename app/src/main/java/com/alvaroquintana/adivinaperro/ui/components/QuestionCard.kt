package com.alvaroquintana.adivinaperro.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import com.alvaroquintana.adivinaperro.ui.theme.GameWhite
import com.alvaroquintana.adivinaperro.ui.theme.LocalGameColors

@Composable
fun QuestionCard(
    imageUrl: String,
    questionNumber: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier,
    timerProgress: Float = 1f
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = GameWhite,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        ) {
            BreedImage(
                imageData = imageUrl,
                contentDescription = "Question $questionNumber of $totalQuestions",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
