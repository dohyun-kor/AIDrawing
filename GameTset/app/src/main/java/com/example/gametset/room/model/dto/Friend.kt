package com.example.gametset.room.model.dto

data class Friend(
    val userId: Int,
    val friendId: Int,
    val status: String,     // BLOCKED, ACCEPT, PENDING
)