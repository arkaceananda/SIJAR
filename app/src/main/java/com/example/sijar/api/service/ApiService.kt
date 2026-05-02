package com.example.sijar.api.service

import com.example.sijar.api.model.data.request.AuthRequest
import com.example.sijar.api.model.data.response.CreatePeminjamanResponse
import com.example.sijar.api.model.data.response.ItemResponse
import com.example.sijar.api.model.data.response.AuthResponse
import com.example.sijar.api.model.data.response.PeminjamanResponse
import com.example.sijar.api.model.data.response.DashboardResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {
    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("barang")
    suspend fun getItems(): Response<ItemResponse>

    @GET("peminjaman")
    suspend fun getPeminjaman(@Header("Authorization") token: String): Response<PeminjamanResponse>

    @GET("homepage")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @Multipart
    @POST("peminjaman-kirim")
    suspend fun createPeminjaman(
        @Header("Authorization") token: String,
        @Part("keperluan") keperluan: RequestBody,
        @Part("item_id") itemId: RequestBody,
        @Part bukti: MultipartBody.Part?
    ): Response<CreatePeminjamanResponse>
}
