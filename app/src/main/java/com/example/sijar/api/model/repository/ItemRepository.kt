package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.response.ItemResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ItemRepository(private val apiService: ApiService) {
    suspend fun getItems(): ApiResult<ItemResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getItems()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            ApiResult.Success(body)
                        } else {
                            ApiResult.Error(ErrorType.EmptyResponse, "Data barang kosong")
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
