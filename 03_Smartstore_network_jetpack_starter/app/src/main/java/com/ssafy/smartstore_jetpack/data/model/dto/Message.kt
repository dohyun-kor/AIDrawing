package com.ssafy.smartstore_jetpack.data.model.dto

data class Message(
    val content: String = "",
    val refusal: Any? = null,
    val role: String = ""
)