package com.example.sijar.api.utils

sealed class ErrorType {
    object Network: ErrorType() // Network error
    object Unauthorized: ErrorType() // 401
    object Server: ErrorType() // 500
    object BadRequest: ErrorType() // 400
    object Validation: ErrorType() // 422
    object EmptyResponse: ErrorType() // Empty response from server
    object Unknown: ErrorType()
}