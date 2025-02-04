package com.example.gametset.room.model.dto

data class UserDto(
    val userId: Int,
    val email : String,
    val id: String,
    val password: String,
    val nickname: String,
    val point: Int,
    val gamesWon: Int,
    val totalGames: Int,
    val level: Int,
    val exp: Int,
) {
    constructor() : this(0,"", "", "", "", 0, 0, 0, 0, 0)
    constructor(
        userId: Int,
        id: String,
        nickname: String,
        point: Int,
        gamesWon: Int,
        totalGames: Int,
        level: Int,
        exp: Int
    ) : this(userId,"", id, "", nickname, point, gamesWon, totalGames, level, exp)
    constructor(id:String, password: String, nickname: String) : this(0,"",id, password, nickname,0,0,0,0,0)

}