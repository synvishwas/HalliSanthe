package com.example.hallisanthe.model

data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val category: String = "",
    val condition: String = "",
    val description: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val location: String = "",
    val pin: String = "",
    val imageUrl: String = "",
    val isSold: Boolean = false,
    val timestamp: Long = 0L,
    val discount: String = "",
    val isFeatured: Boolean = false,
    val viewCount: Int = 0,
    val enquiryCount: Int = 0,
    val reviewCount: Int = 0,
    val avgRating: Float = 0f,
    val deliveryAvailable: Boolean = false,
    val reportCount: Int = 0,
    val isWishlisted: Boolean = false
)