package com.example.sijar.api.utils

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(val type: ErrorType, val message: String? = null): ApiResult<Nothing>()
}