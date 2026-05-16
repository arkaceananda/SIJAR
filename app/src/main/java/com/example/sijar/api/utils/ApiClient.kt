package com.example.sijar.api.utils

import android.annotation.SuppressLint
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.sijar.api.model.data.response.ItemResponse
import com.example.sijar.api.model.data.response.ItemResponseDeserializer
import com.example.sijar.api.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

@SuppressLint("StaticFieldLeak")
object ApiClient {
    const val BASE_URL = "http://10.0.2.2/api/mobile/"

    private var sessionManager: SessionManager? = null
    private var context: Context? = null

    fun init(context: Context, sessionManager: SessionManager) {
        this.context = context.applicationContext
        this.sessionManager = sessionManager
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.addHeader("Accept", "application/json")
                val token = sessionManager?.getToken()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(logging)
            .addInterceptor(ChuckerInterceptor(context!!))
//            .apply {
//                FlipperInitializer.getInterceptor()?.let { addInterceptor(it) }
//            }
            .build()
    }

    // Custom GSON
    private val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(ItemResponse::class.java, ItemResponseDeserializer())
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}