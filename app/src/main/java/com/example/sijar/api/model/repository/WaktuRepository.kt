package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class WaktuRepository(private val apiService: ApiService) {

    suspend fun getWaktuPembelajaran(): ApiResult<List<WaktuPeminjaman>> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getWaktuPembelajaran()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) ApiResult.Success(body.data)
                        else ApiResult.Error(ErrorType.EmptyResponse)
                    } else {
                        when (response.code()) {
                            401 -> ApiResult.Error(ErrorType.Unauthorized)
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
}