package com.example.sijar.api.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImagePickerHelper {
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return null

            val tempFile = File(context.cacheDir, "bukti_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            tempFile
        } catch (_: Exception) {
            null
        }
    }
}