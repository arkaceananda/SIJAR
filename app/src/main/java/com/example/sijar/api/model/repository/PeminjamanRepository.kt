package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.request.CreatePeminjamanRequest
import com.example.sijar.api.model.data.response.CreatePeminjamanResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class PeminjamanRepository (private val apiService: ApiService){
    suspend fun createPeminjaman(
        keperluan: String,
        itemId: Int,
        kodeUnit: String?,
        waktuIds: List<String>
    ): ApiResult<CreatePeminjamanResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val waktuParts = waktuIds.map { id ->
                        MultipartBody.Part.createFormData("waktu_ids[]", id)
                    }
                    val response = apiService.createPeminjaman(
                        keperluan = keperluan.toRequestBody("text/plain".toMediaType()),
                        itemId = itemId.toString().toRequestBody("text/plain".toMediaType()),
                        kodeUnit = kodeUnit!!.toRequestBody("text/plain".toMediaType()),
                        waktuIds = waktuParts,
                        bukti = null
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            ApiResult.Success(body)
                        } else {
                            ApiResult.Error(ErrorType.EmptyResponse)
                        }
                    } else {
                        val errorType = when (response.code()) {
                            400 -> ErrorType.BadRequest
                            401 -> ErrorType.Unauthorized
                            in 500..599 -> ErrorType.Server
                            else -> ErrorType.Unknown
                        }
                        ApiResult.Error(errorType)
                    }
                } catch (_: IOException) {
                    ApiResult.Error(ErrorType.Network)
                } catch (_: Exception) {
                    ApiResult.Error(ErrorType.Unknown)
                }
            }
        }
    }
}