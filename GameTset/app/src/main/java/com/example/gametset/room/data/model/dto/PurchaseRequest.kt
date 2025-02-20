package com.example.gametset.room.data.model.dto

data class PurchaseRequest(
    val itemId: Int,
    val userId: Int,
    val itemPrice: Int,
    val category: String
)