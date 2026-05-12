package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.response.DashboardResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardRepository(private val apiService: ApiService) {

    suspend fun getDashboardData(): ApiResult<DashboardResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getDashboard()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            ApiResult.Success(body)
                        } else {
                            ApiResult.Error(ErrorType.EmptyResponse)
                        }
                    } else {
                        ApiResult.Error(ErrorType.Unknown)
                    }
                } catch (e: Exception) {
                    ApiResult.Error(ErrorType.Network, e.localizedMessage)
                }
            }
        }
    }
}
