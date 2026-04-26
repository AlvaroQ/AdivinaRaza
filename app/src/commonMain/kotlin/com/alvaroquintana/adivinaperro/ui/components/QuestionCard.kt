package com.alvaroquintana.adivinaperro.ui.components

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.content_desc_question_image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuestionCard(
    imageUrl: String,
    questionNumber: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier,
    imageContentScale: ContentScale = ContentScale.Crop
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        ) {
            BreedImage(
                imageData = imageUrl,
                contentDescription = stringResource(Res.string.content_desc_question_image, questionNumber, totalQuestions),
                modifier = Modifier.fillMaxSize(),
                contentScale = imageContentScale
            )
        }
    }
}
