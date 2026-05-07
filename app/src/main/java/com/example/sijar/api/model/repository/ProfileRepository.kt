package com.example.sijar.api.model.repository

import android.R.attr.type
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
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ProfileRepository(private val apiService: ApiService) {

    suspend fun getProfile(token: String): ApiResult<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getProfile(token)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) ApiResult.Success(body)
                        else ApiResult.Error(ErrorType.EmptyResponse)
                    } else {
                        val type = when(response.code()) {
                            401 -> ErrorType.Unknown
                            500 -> ErrorType.Server
                            else -> ErrorType.Unknown
                        }
                        ApiResult.Error(type)
                    }
                } catch (_: IOException) {
                    ApiResult.Error(ErrorType.Network)
                } catch (_: Exception) {
                    ApiResult.Error(ErrorType.Unknown)
                }
            }
        }
    }

    suspend fun updatePhoto(token: String, photo: MultipartBody.Part): ApiResult<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateProfilePhoto(token, photo)
                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(ErrorType.Unknown)
                }
            } catch (_: IOException) {
                ApiResult.Error(ErrorType.Network)
            } catch (_: Exception) {
                ApiResult.Error(ErrorType.Unknown)
            }
        }
    }

    suspend fun deletePhoto(token: String): ApiResult<ProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteProfilePhoto(token)
                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(ErrorType.Unknown)
                }
            } catch (_: IOException) {
                ApiResult.Error(ErrorType.Network)
            } catch (_: Exception) {
                ApiResult.Error(ErrorType.Unknown)
            }
        }
    }

    suspend fun changePassword(token: String, request: UpdatePasswordRequest): ApiResult<UpdatePasswordResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updatePassword(token, request)
                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    val type = when(response.code()) {
                        400 -> ErrorType.BadRequest
                        401 -> ErrorType.Unauthorized
                        500 -> ErrorType.Server
                        else -> ErrorType.Unknown
                    }
                    ApiResult.Error(type)
                }
            } catch (_: IOException) {
                ApiResult.Error(ErrorType.Network)
            } catch (_: Exception) {
                ApiResult.Error(ErrorType.Unknown)
            }
        }
    }

    suspend fun updateProfile(
        token: String,
        userId: Int,
        name: String,
        email: String,
        telepon: String?
    ): ApiResult<UpdateProfileResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val namePart = name.toRequestBody("text/plain".toMediaType())
                val emailPart = email.toRequestBody("text/plain".toMediaType())
                val teleponPart = telepon?.toRequestBody("text/plain".toMediaType())
                val methodPart = "PUT".toRequestBody("text/plain".toMediaType())

                val response = apiService.updateProfile(
                    token = token,
                    userId = userId,
                    name = namePart,
                    email = emailPart,
                    telepon = teleponPart,
                    method = methodPart
                )

                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    val type = when (response.code()) {
                        401  -> ErrorType.Unauthorized
                        422  -> ErrorType.Validation
                        404  -> ErrorType.Unknown
                        500  -> ErrorType.Server
                        else -> ErrorType.Unknown
                    }
                    val errorMsg = try {
                        response.errorBody()?.string()
                    } catch (_: Exception) { null }

                    ApiResult.Error(type, errorMsg)
                }
            } catch (_: IOException) {
                ApiResult.Error(ErrorType.Network)
            } catch (_: Exception) {
                ApiResult.Error(ErrorType.Unknown)
            }
        }
    }
}
