package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.WaktuPeminjaman

data class WaktuResponse(
    val status: Boolean,
    val message: String,
    val data: List<WaktuPeminjaman>
)
