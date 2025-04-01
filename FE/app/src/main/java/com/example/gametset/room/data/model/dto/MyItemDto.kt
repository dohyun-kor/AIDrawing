package com.example.gametset.room.data.model.dto

data class MyItemDto(
    val purchaseId: Int,
    val itemId: Int,
    val userId: Int,
    val itemName: String,
    val category: String?,
    val purchaseDate: String
)

