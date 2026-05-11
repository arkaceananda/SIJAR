package com.example.sijar.api.model.data.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)

data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("kode")
    val kode: String,
    @SerializedName("kelas")
    val kelas: String? = null,
    @SerializedName("telepon")
    val telepon: String?,
    @SerializedName("profile")
    val profile: String?,
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("jurusan_id")
    val jurusanId: Int,
    @SerializedName("kategori_id")
    val kategoriId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
