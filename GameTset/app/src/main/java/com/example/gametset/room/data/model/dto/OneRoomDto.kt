package com.example.gametset.room.data.model.dto

data class OneRoomDto (
    var roomId: Int,
    val hostId: Int,
    val roomName: String,
    val status: String,
    val nowPlayers: Int,
    val maxPlayers: Int,
    val rounds : Int,
    val mode : String,
    val level : String,
    val roundTime : Int
)