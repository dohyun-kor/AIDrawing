package com.ssafy.smartstore_jetpack.data.model.dto

data class Choice(
    val finish_reason: String = "",
    val index: Int = 0,
    val logprobs: Any? = null,
    val message: Message = Message()
)