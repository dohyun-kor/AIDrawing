package com.example.gametset.room.data.model.dto

data class RoomChangeDto(
    val roomId: Int,
    val hostId: Int,
    val roomName: String,
    val status: String,
    val maxPlayers: Int,
    val rounds: Int,
    val mode: String,
    val level: String,
    val roundTime: Int
)
