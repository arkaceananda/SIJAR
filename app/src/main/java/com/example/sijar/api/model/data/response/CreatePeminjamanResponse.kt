package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Peminjaman

data class CreatePeminjamanResponse(
    val status: Boolean,
    val message: String,
    val `data`: Peminjaman? = null
)
