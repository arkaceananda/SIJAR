package com.example.sijar.api.model.repository

import android.util.Log
import com.example.sijar.api.model.data.response.ItemResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ItemRepository(private val apiService: ApiService) {
    suspend fun getItems(
        kategoriId: Int? = null,
        search: String? = null
    ): ApiResult<ItemResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response = apiService.getItems(kategoriId, search)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            ApiResult.Success(body)
                        } else {
                            ApiResult.Error(ErrorType.EmptyResponse)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        ApiResult.Error(ErrorType.Unknown, errorBody)
                    }
                } catch (e: IOException) {
                    ApiResult.Error(ErrorType.Network)
                } catch (e: Exception) {
                    ApiResult.Error(ErrorType.Unknown)
                }
            }
        }
    }
}
