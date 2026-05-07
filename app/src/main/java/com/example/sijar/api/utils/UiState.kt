package com.example.sijar.api.utils

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object Idle: UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val type: ErrorType, val message: String? = null) : UiState<Nothing>()
}