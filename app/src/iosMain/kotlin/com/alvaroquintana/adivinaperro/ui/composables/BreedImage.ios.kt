package com.alvaroquintana.adivinaperro.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.skia.Image

@OptIn(ExperimentalEncodingApi::class)
@Composable
actual fun rememberBase64ImageBitmap(data: String): ImageBitmap? = remember(data) {
    try {
        val bytes = Base64.decode(data)
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (_: Throwable) {
        null
    }
}
