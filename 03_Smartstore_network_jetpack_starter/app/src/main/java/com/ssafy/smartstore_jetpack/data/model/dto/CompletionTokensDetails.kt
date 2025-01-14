package com.ssafy.smartstore_jetpack.data.model.dto

data class CompletionTokensDetails(
    val accepted_prediction_tokens: Int = 0,
    val audio_tokens: Int = 0,
    val reasoning_tokens: Int = 0,
    val rejected_prediction_tokens: Int = 0
)