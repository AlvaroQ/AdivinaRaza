package com.alvaroquintana.adivinaperro.ui.composables

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

@Composable
actual fun rememberBase64ImageBitmap(data: String): ImageBitmap? = remember(data) {
    try {
        val bytes = Base64.decode(data, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    } catch (e: Exception) {
        Log.e("BreedImage", "Failed to decode base64 image", e)
        null
    }
}
