package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.request.UpdatePasswordRequest
import com.example.sijar.api.model.data.response.ProfileResponse
import com.example.sijar.api.model.data.response.UpdatePasswordResponse
import com.example.sijar.api.model.data.response.UpdateProfileResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProfileRepository(private val apiService: ApiService) {

    suspend fun getProfile(): ApiResult<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getProfile()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) ApiResult.Success(body)
                        else ApiResult.Error(ErrorType.EmptyResponse)
                    } else {
                        ApiResult.Error(ErrorType.Unknown)
                    }
                } catch (e: Exception) {
                    ApiResult.Error(ErrorType.Network, e.localizedMessage)
                }
            }
        }
    }

    suspend fun changePassword(request: UpdatePasswordRequest): ApiResult<UpdatePasswordResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updatePassword(request)
                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(ErrorType.BadRequest)
                }
            } catch (e: Exception) {
                ApiResult.Error(ErrorType.Network, e.localizedMessage)
            }
        }
    }

    suspend fun updateProfile(
        userId: Int,
        name: String,
        kode: String,
        telepon: String?,
        removePhoto: Boolean,
        photoFile: File?
    ): ApiResult<UpdateProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val kodePart = kode.toRequestBody("text/plain".toMediaType())
                val teleponPart = (telepon ?: "").toRequestBody("text/plain".toMediaType())
                val removePhotoPart = removePhoto.toString().toRequestBody("text/plain".toMediaType())
                
                val photoPart = photoFile?.let { 
                    val body = it.asRequestBody("image/jpeg".toMediaType())
                    MultipartBody.Part.createFormData("profile", it.name, body)
                }

                val response = apiService.updateProfile(
                    userId = userId,
                    name = namePart,
                    kode = kodePart,
                    telepon = teleponPart,
                    removePhoto = removePhotoPart,
                    profile = photoPart
                )

                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(ErrorType.Validation, response.errorBody()?.string())
                }
            } catch (e: Exception) {
                ApiResult.Error(ErrorType.Network, e.localizedMessage)
            }
        }
    }
}
