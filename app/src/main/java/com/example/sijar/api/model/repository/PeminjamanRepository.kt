package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.response.CreatePeminjamanResponse
import com.example.sijar.api.model.data.response.PeminjamanResponse
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
import java.io.IOException

class PeminjamanRepository(private val apiService: ApiService) {

    suspend fun getPeminjamanList(token: String): ApiResult<PeminjamanResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getPeminjamanList()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) ApiResult.Success(body)
                        else ApiResult.Error(ErrorType.EmptyResponse)
                    } else {
                        when (response.code()) {
                            401 -> ApiResult.Error(ErrorType.Unauthorized)
                            404 -> ApiResult.Error(ErrorType.NotFound)
                            in 500..599 -> ApiResult.Error(ErrorType.Server)
                            else -> ApiResult.Error(ErrorType.Unknown)
                        }
                    }
                } catch (_: IOException) {
                    ApiResult.Error(ErrorType.Network)
                } catch (_: Exception) {
                    ApiResult.Error(ErrorType.Unknown)
                }
            }
        }
    }

    suspend fun createPeminjaman(
        token: String,
        keperluan: String,
        itemId: Int,
        kodeUnit: String?,
        waktuIds: List<String>,
        buktiFoto: File?
    ): ApiResult<CreatePeminjamanResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val keperluanBody = keperluan.toRequestBody("text/plain".toMediaType())
                val itemIdBody = itemId.toString().toRequestBody("text/plain".toMediaType())
                val kodeUnitBody = (kodeUnit ?: "").toRequestBody("text/plain".toMediaType())

                // Laravel membaca array sebagai waktu_ids[0], waktu_ids[1], dst.
                val waktuParts = waktuIds.mapIndexed { index, json ->
                    MultipartBody.Part.createFormData("waktu_ids[$index]", json)
                }

                val buktiFotoPart = buktiFoto?.let {
                    val body = it.asRequestBody("image/jpeg".toMediaType())
                    MultipartBody.Part.createFormData("bukti", it.name, body)
                }

                val response = apiService.createPeminjaman(
                    keperluan = keperluanBody,
                    itemId = itemIdBody,
                    kodeUnit = kodeUnitBody,
                    waktuIds = waktuParts,
                    bukti = buktiFotoPart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) ApiResult.Success(body)
                    else ApiResult.Error(ErrorType.EmptyResponse)
                } else {
                    when (response.code()) {
                        401 -> ApiResult.Error(ErrorType.Unauthorized)
                        422 -> ApiResult.Error(ErrorType.Validation)
                        in 500..599 -> ApiResult.Error(ErrorType.Server)
                        else -> ApiResult.Error(ErrorType.Unknown)
                    }
                }
            } catch (_: IOException) {
                ApiResult.Error(ErrorType.Network)
            } catch (_: Exception) {
                ApiResult.Error(ErrorType.Unknown)
            }
        }
    }
}