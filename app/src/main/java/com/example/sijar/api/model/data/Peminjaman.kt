package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName

data class Peminjaman(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("item_id")
    val itemId: Int,
    val keperluan: String? = null,
    @SerializedName("status_tujuan")
    val statusTujuan: String? = null,
    @SerializedName("status_pinjaman")
    val statusPinjaman: String? = null,
    val status: String? = null,
    @SerializedName("kode_unit")
    val kodeUnit: String? = null,
    @SerializedName("jam_pembelajaran")
    val jamPembelajaran: String? = null,
    @SerializedName("gambar_bukti")
    val gambarBukti: String? = null,
    val bukti: String? = null,
    val tanggal: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val item: Item? = null
)
