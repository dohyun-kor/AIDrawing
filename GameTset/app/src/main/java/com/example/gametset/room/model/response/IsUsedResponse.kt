package com.example.gametset.room.model.response

import com.google.gson.annotations.SerializedName

data class IsUsedResponse (
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data,
)

data class Data(
    val duplicated:Boolean,
)