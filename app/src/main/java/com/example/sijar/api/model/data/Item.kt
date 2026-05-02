package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName

data class Item(
    val id: Int,
    @SerializedName("nama_item")
    val namaItem: String? = null,
    @SerializedName("jenis_item")
    val jenisItem: String? = null,
    @SerializedName("status_item")
    val statusItem: String? = null,
    @SerializedName("kategori_jurusan_id")
    val kategoriJurusanId: Int? = null,
    @SerializedName("kode_unit")
    val kodeUnit: String? = null,
    @SerializedName("deskripsi_item")
    val deskripsiItem: String? = null,
    @SerializedName("foto_barang")
    val fotoBarang: String? = null,
    @SerializedName("kategori_jurusan")
    val kategoriJurusan: Kategori? = null
)
