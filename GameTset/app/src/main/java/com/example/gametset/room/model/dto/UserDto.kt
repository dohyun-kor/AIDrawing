package com.example.gametset.room.model.dto

data class UserDto(
    val userId: Int,
    val id: String,
    val password: String,
    val nickname: String,
<<<<<<< Updated upstream
    val point: Int,
    val gamesWon: Int,
    val totalGames: Int,
    val level: Int,
    val exp: Int,
) {
    constructor() : this(0, "", "", "", 0, 0, 0, 0, 0)
    constructor(
        userId: Int,
        id: String,
        nickname: String,
        point: Int,
        gamesWon: Int,
        totalGames: Int,
        level: Int,
        exp: Int
    ) : this(userId, id, "", nickname, point, gamesWon, totalGames, level, exp)
=======
    val email: String? = null
){
    constructor() : this("", "", "")
    constructor(id: String, pass: String) : this(id, pass,"")
>>>>>>> Stashed changes
}