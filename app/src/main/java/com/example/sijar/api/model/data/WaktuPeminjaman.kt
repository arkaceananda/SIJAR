package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName

data class WaktuPeminjaman(
    @SerializedName("jam_ke")
    val jamKe: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String
)
