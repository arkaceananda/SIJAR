package com.example.sijar.api.model.data.request

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    val email: String,
    val password: String
)
