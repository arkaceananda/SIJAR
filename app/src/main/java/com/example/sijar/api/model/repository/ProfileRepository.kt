package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.response.ProfileResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
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
                } catch (e: IOException) {
                    ApiResult.Error(ErrorType.Network, e.message)
                } catch (e: Exception) {
                    ApiResult.Error(ErrorType.Unknown, e.message)
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
            } catch (e: IOException) {
                ApiResult.Error(ErrorType.Network, e.message)
            } catch (e: Exception) {
                ApiResult.Error(ErrorType.Unknown, e.message)
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
            } catch (e: IOException) {
                ApiResult.Error(ErrorType.Network, e.message)
            } catch (e: Exception) {
                ApiResult.Error(ErrorType.Unknown, e.message)
            }
        }
    }
}
