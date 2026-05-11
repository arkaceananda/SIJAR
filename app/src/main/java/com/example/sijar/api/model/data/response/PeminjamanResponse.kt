package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Peminjaman
import com.google.gson.annotations.SerializedName

data class PeminjamanResponse(
    val status: Boolean,
    val message: String,
    @SerializedName("data")
    val paginator: PeminjamanPagingData
)

data class PeminjamanPagingData(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("data")
    val list: List<Peminjaman>,
    @SerializedName("last_page")
    val lastPage: Int
)
