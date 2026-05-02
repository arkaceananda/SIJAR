package com.example.sijar.api.utils

import kotlinx.coroutines.delay

/**
 * Fungsi utility untuk melakukan retry pada API call.
 * Hanya melakukan retry jika error bersifat sementara (Network atau Server Error).
 */
suspend fun <T> retryCall(
    times: Int = 3,
    initialDelay: Long = 1000,
    block: suspend () -> ApiResult<T>
): ApiResult<T> {
    var currentDelay = initialDelay
    repeat(times - 1) {
        val result = block()
        
        if (result is ApiResult.Success) return result

        // cek apakah layak untuk diulang
        if (result is ApiResult.Error) {
            when (result.type) {
                ErrorType.Network, ErrorType.Server -> {
                    delay(currentDelay)
                    currentDelay *= 2
                }
                else -> return result 
            }
        }
    }
    return block()
}
