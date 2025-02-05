package com.example.gametset.room.data.model.dto

data class Friend(
    val userId: Int,
    val friendId: Int,
    val status: String,     // BLOCKED, ACCEPT, PENDING
)