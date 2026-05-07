package com.example.sijar.api.model.data.request

import com.google.gson.annotations.SerializedName

data class UpdatePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String?
)
