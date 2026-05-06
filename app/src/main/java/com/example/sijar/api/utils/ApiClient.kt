package com.example.sijar.api.utils

import com.example.sijar.api.model.data.response.ItemResponse
import com.example.sijar.api.model.data.response.ItemResponseDeserializer
import com.example.sijar.api.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "http://10.0.2.2:8000/api/test/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Custom Gson dengan deserializer untuk ItemResponse
    private val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(ItemResponse::class.java, ItemResponseDeserializer())
            .setLenient()
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
