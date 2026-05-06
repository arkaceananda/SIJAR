package com.example.sijar.api.service

import com.example.sijar.api.model.data.request.AuthRequest
import com.example.sijar.api.model.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("barang")
    suspend fun getItems(
        @Query("kategori_jurusan_id") kategoriId: Int?,
        @Query("search") search: String?
    ): Response<ItemResponse>

    @GET("peminjaman")
    suspend fun getPeminjaman(@Header("Authorization") token: String): Response<PeminjamanResponse>

    @Multipart
    @POST("peminjaman-kirim")
    suspend fun createPeminjaman(
        @Header("Authorization") token: String,
        @Part("keperluan") keperluan: RequestBody,
        @Part("item_id") itemId: RequestBody,
        @Part bukti: MultipartBody.Part?
    ): Response<CreatePeminjamanResponse>

    @GET("homepage")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @Multipart
    @POST("profile/update-photo")
    suspend fun updateProfilePhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<ProfileResponse>

    @DELETE("profile/delete-photo")
    suspend fun deleteProfilePhoto(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

//    @PUT("password")
//    suspend fun updatePassword(
//        @Header("Authorization") token: String,
//        @Body request: UpdatePasswordRequest
//    ): Response<UpdatePasswordResponse>
}
