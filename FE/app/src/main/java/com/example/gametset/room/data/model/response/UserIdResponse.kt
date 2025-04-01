package com.example.gametset.room.data.model.response

import com.google.gson.annotations.SerializedName

data class UserIdResponse(
    @SerializedName("userId") val userId: Int,
)