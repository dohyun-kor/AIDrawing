package com.example.gametset.room.data.model.dto

data class RoomDto (
    val roomId: Int,
    val hostId: Int,
    val roomName: String,
    val status: String,
    val maxPlayers: Int,
    val nowPlayers: Int

)

