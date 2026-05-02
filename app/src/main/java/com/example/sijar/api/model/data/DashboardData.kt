package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName


data class DashboardData(
    @SerializedName("peminjamanTerbaru")
    val peminjamanTerbaru: List<Peminjaman>,
    @SerializedName("totalDipinjam")
    val totalDipinjam: Int,
    @SerializedName("totalSelesai")
    val totalSelesai: Int
)
