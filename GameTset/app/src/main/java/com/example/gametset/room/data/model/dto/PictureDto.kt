package com.example.gametset.room.data.model.dto

// 데이터 모델
data class PictureDto(
    val pictureId: Int,
    val userId: Int,
    val imageUrl: String,
    val topic: String = "",
    val title: String = "",
    val description: String,
    val rotation: Int,
    val furniture: Int,  // 액자 아이템 ID
    val xval: Float,
    val yval: Float,
    val displayed: Boolean,
)