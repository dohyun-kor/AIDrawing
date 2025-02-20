package com.example.gametset.room.data.model.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val userId: Int = 0,
    val email: String = "",
    val id: String = "",
    val password: String = "",
    val nickname: String = "",
    @SerializedName("points") val point: Int = 0,
    val gamesWon: Int = 0,
    val totalGames: Int = 0,
    val level: Int = 0,
    val exp: Int = 0,
    val userProfileItemId: Int = 0,
    val token: String = ""
) {
    constructor() : this(0, "", "", "", "", 0, 0, 0, 0, 0,1,"")
    constructor(
        userId: Int,
        id: String,
        nickname: String,
        point: Int,
        gamesWon: Int,
        totalGames: Int,
        level: Int,
        exp: Int
    ) : this(userId, "", id, "", nickname, point, gamesWon, totalGames, level, exp,1,"")

    constructor(id: String, password: String, nickname: String) : this(
        0,
        "",
        id,
        password,
        nickname,
        0,
        0,
        0,
        0,
        0,
        1,
        ""
    )

    constructor(id: String, password: String) : this(0, "", id, password, "", 0, 0, 0, 0, 0,1,"")
}