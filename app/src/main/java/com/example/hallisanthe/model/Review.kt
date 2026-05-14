package com.example.hallisanthe.model

data class Review(
    val id: String = "",
    val productId: String = "",
    val reviewerName: String = "",
    val reviewerPhone: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Long = 0L
)