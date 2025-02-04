package com.example.gametset.room.model.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("points") val points: Int,
    @SerializedName("gameswon") val gameswon: Int,
    @SerializedName("totalgames") val totalgames: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("exp") val exp: Int,
    @SerializedName("createdAt") val createdAt: String,
)