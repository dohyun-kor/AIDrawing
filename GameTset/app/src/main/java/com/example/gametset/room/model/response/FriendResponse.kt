package com.example.gametset.room.model.response

import com.google.gson.annotations.SerializedName

data class FriendResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("friendId") val friendId: Int,
    @SerializedName("status") val status: String
)