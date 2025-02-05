package com.example.gametset.room.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val userId: Int,
    val token : String,
)