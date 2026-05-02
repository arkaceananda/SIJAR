package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Peminjaman

data class PeminjamanResponse(
    val status: Boolean,
    val message: String,
    val peminjaman: List<Peminjaman>
)
