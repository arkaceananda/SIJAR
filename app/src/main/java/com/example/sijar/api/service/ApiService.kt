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
    suspend fun getWaktuPembelajaran(): Response<WaktuResponse>

    @GET("peminjaman")
    suspend fun getPeminjamanList(): Response<PeminjamanResponse>

    @Multipart
    @POST("peminjaman/store")
    suspend fun createPeminjaman(
        @Part("keperluan") keperluan: RequestBody,
        @Part("item_id") itemId: RequestBody,
        @Part("kode_unit") kodeUnit: RequestBody,
        @Part waktuIds: List<MultipartBody.Part>,
        @Part bukti: MultipartBody.Part?
    ): Response<CreatePeminjamanResponse>

    @GET("homepage")
    suspend fun getDashboard(): Response<DashboardResponse>

    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PATCH("profile/password/update")
    suspend fun updatePassword(
        @Body request: UpdatePasswordRequest
    ): Response<UpdatePasswordResponse>

    @Multipart
    @POST("profile/update/{id}")
    suspend fun updateProfile(
        @Path("id") userId: Int,
        @Part("name") name: RequestBody,
        @Part("kode") kode: RequestBody,
        @Part("telepon") telepon: RequestBody?,
        @Part("remove_photo") removePhoto: RequestBody?,
        @Part profile: MultipartBody.Part?,
        @Part("_method") method: RequestBody = "PUT".toRequestBody("text/plain".toMediaType())
    ): Response<UpdateProfileResponse>
}
