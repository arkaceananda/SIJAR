package com.example.sijar

import android.content.Context
import okhttp3.Interceptor

object FlipperInitializer {
    fun init(context: Context) {
        // No-op for release
    }

    fun getInterceptor(): Interceptor? {
        return null
    }
}
