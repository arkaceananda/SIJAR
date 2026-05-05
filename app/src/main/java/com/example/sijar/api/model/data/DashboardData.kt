package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName


data class DashboardData(
    @SerializedName("riwayat_terakhir")
    val peminjamanTerbaru: List<Peminjaman>,
    @SerializedName("total_dipinjam")
    val totalDipinjam: Int,
    @SerializedName("total_selesai")
    val totalSelesai: Int
)
