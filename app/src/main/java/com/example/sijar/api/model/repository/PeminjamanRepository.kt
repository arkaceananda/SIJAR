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

class PeminjamanRepository(private val apiService: ApiService) {

    suspend fun getPeminjamanList(): ApiResult<PeminjamanResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getPeminjamanList()
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

    suspend fun createPeminjaman(
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

                val waktuParts = waktuIds.mapIndexed { index, json ->
                    MultipartBody.Part.createFormData("waktu_ids[$index]", json)
                }

                val buktiFotoPart = buktiFoto?.let {
                    val body = it.asRequestBody("image/*".toMediaType())
                    MultipartBody.Part.createFormData("bukti", it.name, body)
                }

                val response = apiService.createPeminjaman(
                    keperluan = keperluanBody,
                    itemId = itemIdBody,
                    kodeUnit = kodeUnitBody,
                    waktuIds = waktuParts,
                    bukti = buktiFotoPart
                )

                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string()
                    ApiResult.Error(ErrorType.Validation, errorMsg)
                }
            } catch (e: Exception) {
                ApiResult.Error(ErrorType.Unknown, e.localizedMessage)
            }
        }
    }
}
