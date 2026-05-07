package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Item
import com.google.gson.annotations.SerializedName

data class ItemResponse(
    @SerializedName("status")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val paginator: PagingData,
    @SerializedName("Totalbarangjurusan")
    val total: Int
)

data class PagingData(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("data")
    val barangList: List<Item>,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("total")
    val total: Int
)
