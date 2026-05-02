package com.example.sijar.api.model.repository

import com.example.sijar.api.model.data.request.AuthRequest
import com.example.sijar.api.model.data.response.AuthResponse
import com.example.sijar.api.service.ApiService
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.retryCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(kodeKelas: String, password: String): ApiResult<AuthResponse> {
        return withContext(Dispatchers.IO) {
            retryCall {
                try {
                    val response =
                        apiService.login(AuthRequest(email = kodeKelas, password = password))

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            ApiResult.Success(body)
                        } else {
                            ApiResult.Error(ErrorType.Unknown, "Response Body Kosong")
                        }
                    } else {
                        val errorType = when (response.code()) {
                            401 -> ErrorType.Unauthorized
                            400 -> ErrorType.BadRequest
                            in 500..599 -> ErrorType.Server
                            else -> ErrorType.Unknown
                        }
                        ApiResult.Error(errorType, response.message())
                    }
                } catch (e: IOException) {
                    ApiResult.Error(ErrorType.Network, e.message)
                } catch (e: retrofit2.HttpException) {
                    ApiResult.Error(ErrorType.Server, e.message)
                } catch (e: Exception) {
                    ApiResult.Error(ErrorType.Unknown, e.message)
                }
            }
        }
    }
}