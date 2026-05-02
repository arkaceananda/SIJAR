package com.example.sijar.api.model.data.response

data class ApiErroResponse(
    val status: Boolean = false,
    val message: String,
    val errors: Map<String, List<String>>? = null
)
