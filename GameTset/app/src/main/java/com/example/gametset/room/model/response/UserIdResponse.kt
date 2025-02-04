package com.example.gametset.room.model.response

import com.google.gson.annotations.SerializedName

data class UserIdResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("userId") val userId: Int,
)