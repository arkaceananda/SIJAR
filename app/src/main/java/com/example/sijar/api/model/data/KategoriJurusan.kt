package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName

data class KategoriJurusan(
    val id: Int,
    @SerializedName("nama_kategori")
    val namaKategori: String?
)
