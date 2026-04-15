package com.alvaroquintana.adivinaperro.screenshot

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.test.platform.app.InstrumentationRegistry

object ScreenshotHelper {

    fun captureAndSave(node: SemanticsNodeInteraction, fileName: String) {
        val bitmap = node.captureToImage().asAndroidBitmap()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(bitmap, fileName)
        } else {
            @Suppress("DEPRECATION")
            saveToPicturesDir(bitmap, fileName)
        }
    }

    private fun saveViaMediaStore(bitmap: Bitmap, fileName: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resolver = context.contentResolver
        val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/capture"

        // Delete any existing file with the same name to avoid (1), (2) copies
        resolver.delete(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            "${MediaStore.Downloads.DISPLAY_NAME} = ? AND ${MediaStore.Downloads.RELATIVE_PATH} = ?",
            arrayOf("$fileName.png", "$relativePath/")
        )

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.Downloads.MIME_TYPE, "image/png")
            put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
            }
            println("Screenshot saved: Downloads/capture/$fileName.png")
        } ?: println("ERROR: Failed to create MediaStore entry for $fileName")
    }

    private fun saveToPicturesDir(bitmap: Bitmap, fileName: String) {
        @Suppress("DEPRECATION")
        val dir = java.io.File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "capture"
        )
        if (!dir.exists()) dir.mkdirs()
        val file = java.io.File(dir, "$fileName.png")
        java.io.FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
        }
        println("Screenshot saved: ${file.absolutePath}")
    }
}
