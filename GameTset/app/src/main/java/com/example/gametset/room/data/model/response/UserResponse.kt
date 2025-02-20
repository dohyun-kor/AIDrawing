package com.example.gametset.room.data.model.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("points") val points: Int,
    @SerializedName("gamesWon") val gameswon: Int,
    @SerializedName("totalGames") val totalgames: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("exp") val exp: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("userProfileItemId") val userProfileItemId: Int,
){
    constructor():this(0,"","","",0,0,0,0,0,"",0)
}