package com.example.sijar.api.model.data.request

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("kode")
    val kode: String,
    val password: String
)
