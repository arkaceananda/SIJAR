package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Item
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

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
