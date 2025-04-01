package com.example.gametset.room.data.model

data class GuestBook(
    val visitorName: String,
    val message: String,
    val rating: Float,
    val date: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isLiked: Boolean = false
) 