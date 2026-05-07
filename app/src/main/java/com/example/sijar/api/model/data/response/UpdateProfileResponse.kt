package com.example.sijar.api.model.data.response

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ProfileResponse?
)
