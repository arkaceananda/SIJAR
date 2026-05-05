package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.DashboardData

data class DashboardResponse(
    val status: Boolean,
    val message: String,
    val `data`: DashboardData
)
