package com.alvaroquintana.adivinaperro.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
expect fun rememberBase64ImageBitmap(data: String): ImageBitmap?

/**
 * Unified image component for the app.
 * Handles both HTTP URLs (via Coil) and base64-encoded image data (manual decode).
 */
@Composable
fun BreedImage(
    imageData: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (imageData.isBlank()) return

    if (imageData.startsWith("http")) {
        AsyncImage(
            model = imageData,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        val bitmap = rememberBase64ImageBitmap(imageData)
        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}
