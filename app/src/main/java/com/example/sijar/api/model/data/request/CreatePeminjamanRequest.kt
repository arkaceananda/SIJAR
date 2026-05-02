package com.example.sijar.api.model.data.request

import com.google.gson.annotations.SerializedName

data class CreatePeminjamanRequest(
    val keperluan: String,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("kode_unit")
    val kodeUnit: String? = null,
    @SerializedName("waktu_ids")
    val waktuIds: List<String>
)
