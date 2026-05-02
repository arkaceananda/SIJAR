package com.example.sijar.api.model.data

import com.google.gson.annotations.SerializedName

data class Kategori(
    val id: Int,
    @SerializedName("nama_kategori") val namaKategori: String
)
