package com.example.sijar.api.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val fileName = "profile_image_${System.currentTimeMillis()}.jpg"
    val file = File(context.cacheDir, fileName)
    
    try {
        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    val requestFile = file.asRequestBody(contentResolver.getType(fileUri)?.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}
