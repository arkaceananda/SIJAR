package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName


data class DashboardData(
    @SerializedName("peminjaman_terbaru")
    val peminjamanTerbaru: List<Peminjaman> = emptyList(),
    @SerializedName("total_dipinjam")
    val totalDipinjam: Int = 0,
    @SerializedName("total_selesai")
    val totalSelesai: Int = 0
)
