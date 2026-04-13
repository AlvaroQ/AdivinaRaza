package com.alvaroquintana.adivinaperro.ui.composables

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

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
        val bitmap = remember(imageData) {
            try {
                val bytes = Base64.decode(imageData, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                Log.e("BreedImage", "Failed to decode base64 image", e)
                null
            }
        }
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }
}
