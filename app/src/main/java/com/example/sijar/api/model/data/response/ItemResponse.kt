package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Item

data class ItemResponse(
    val success: Boolean,
    val message: String,
    val data: List<Item>
)