package com.example.sijar.api.model.data.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("data")
    val data: List<Data>,
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
    @SerializedName("email")
    val email: String,
    @SerializedName("kelas")
    val kelas: String,
    @SerializedName("telepon")
    val telepon: String?,
    @SerializedName("profile")
    val profile: String?,
    @SerializedName("role")
    val role: String,
    @SerializedName("jurusan_id")
    val jurusanId: Int,
    @SerializedName("kategori_id")
    val kategoriId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
