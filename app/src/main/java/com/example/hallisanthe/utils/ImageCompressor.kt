package com.example.hallisanthe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageCompressor {

    fun compressToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val original    = BitmapFactory.decodeStream(inputStream)

        // ✅ Increased to 800px for much better clarity
        val maxSize = 800
        val ratio   = minOf(
            maxSize.toFloat() / original.width,
            maxSize.toFloat() / original.height
        )

        // Don't upscale if image is smaller
        val finalRatio = if (ratio > 1f) 1f else ratio

        val newWidth  = (original.width  * finalRatio).toInt()
        val newHeight = (original.height * finalRatio).toInt()

        val resized = Bitmap.createScaledBitmap(
            original, newWidth, newHeight, true
        )

        // ✅ Increased to 90% quality for clearest images
        val outputStream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        return Base64.encodeToString(
            outputStream.toByteArray(),
            Base64.DEFAULT
        )
    }
}