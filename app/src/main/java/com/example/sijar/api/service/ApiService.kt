package com.example.sijar.api.service

import com.example.sijar.api.model.data.Peminjaman
import com.example.sijar.api.model.data.request.AuthRequest
import com.example.sijar.api.model.data.request.UpdatePasswordRequest
import com.example.sijar.api.model.data.response.*
import com.example.sijar.api.model.data.response.UpdatePasswordResponse
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
        @Query("search") search: String?
    ): Response<ItemResponse>

    @GET("peminjaman")
    suspend fun getPeminjamanList (): Response<PeminjamanResponse>

    @Multipart
    @POST("peminjaman-kirim")
    suspend fun createPeminjaman(
        @Part("keperluan") keperluan: RequestBody,
        @Part("item_id") itemId: RequestBody,
        @Part("kode_unit") kodeUnit: RequestBody,
        @Part("waktu_ids") waktuIds: List<MultipartBody.Part>,
        @Part bukti: MultipartBody.Part?
    ): Response<CreatePeminjamanResponse>

    @Multipart
    @GET("peminjaman")
    suspend fun getPeminjaman(
        @Part("keperluan") keperluan: RequestBody,
        @Part("item_id") itemId: RequestBody,
        @Part("kode_unit") kodeUnit: RequestBody,
        @Part("waktu_ids") waktuIds: List<MultipartBody.Part>,
        @Part bukti: MultipartBody.Part?
    ): Response<Peminjaman>

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

    @PUT("password")
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
        @Part("email") email: RequestBody,
        @Part("telepon") telepon: RequestBody?,
        @Part("_method") method: RequestBody = "PUT".toRequestBody("text/plain".toMediaType())
    ): Response<UpdateProfileResponse>
}
