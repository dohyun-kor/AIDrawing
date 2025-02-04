package com.example.gametset.room.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("access") val access: Boolean,
    @SerializedName("userId") val userId: Int,
)