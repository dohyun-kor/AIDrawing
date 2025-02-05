package com.example.gametset.room.model.dto

data class StoreDto(
    val data: List<ItemData>
) {
    data class ItemData(
        val itemId: String,
        val name: String,
        val category: String,
        val price: Int,
        val description: String
    )
}