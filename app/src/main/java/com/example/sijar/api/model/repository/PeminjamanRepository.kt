package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.response.DashboardResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class PeminjamanRepository(private val apiService: ApiService) {

    suspend fun getDashboardData(token: String): ApiResult<DashboardResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getDashboard(token)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            ApiResult.Success(body)
                        } else {
                            ApiResult.Error(ErrorType.EmptyResponse, "Response body null")
                        }
                    } else {
                        ApiResult.Error(ErrorType.Unknown, "Error ${response.code()}")
                    }
                } catch (e: IOException) {
                    ApiResult.Error(ErrorType.Network, e.message)
                } catch (e: Exception) {
                    ApiResult.Error(ErrorType.Unknown, e.message)
                }
            }
        }
    }
}
