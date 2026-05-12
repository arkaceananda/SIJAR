package com.example.sijar.api.service

import com.example.sijar.api.model.data.request.AuthRequest
import com.example.sijar.api.model.data.request.UpdatePasswordRequest
import com.example.sijar.api.model.data.response.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("barang")
    suspend fun getItems(
        @Query("kategori_jurusan_id") kategoriId: Int?,
        @Query("search") search: String?,
        @Query("page") page: Int
    ): Response<ItemResponse>

    @GET("waktu")
    suspend fun getWaktuPembelajaran(
        @Header("Authorization") token: String
    ): Response<WaktuResponse>

    @GET("peminjaman")
    suspend fun getPeminjamanList (
        @Header("Authorization") token: String
    ): Response<PeminjamanResponse>

    @Multipart
    @POST("peminjaman/store")
    suspend fun createPeminjaman(
        @Header("Authorization") token: String,
        @Part("keperluan") keperluan: RequestBody,
        @Part("item_id") itemId: RequestBody,
        @Part("kode_unit") kodeUnit: RequestBody,
        @Part("waktu_ids") waktuIds: List<MultipartBody.Part>,
        @Part bukti: MultipartBody.Part?
    ): Response<CreatePeminjamanResponse>

    @GET("homepage")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @PATCH("profile/password/update")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<UpdatePasswordResponse>

    @Multipart
    @POST("profile/update/{id}")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Part("name") name: RequestBody,
        @Part("kode") kode: RequestBody,
        @Part("telepon") telepon: RequestBody?,
        @Part("remove_photo") removePhoto: RequestBody?,
        @Part profile: MultipartBody.Part?,
        @Part("_method") method: RequestBody = "PUT".toRequestBody("text/plain".toMediaType())
    ): Response<UpdateProfileResponse>
}
