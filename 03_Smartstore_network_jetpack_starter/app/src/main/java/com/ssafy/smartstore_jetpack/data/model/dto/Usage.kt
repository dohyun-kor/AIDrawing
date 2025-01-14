package com.ssafy.smartstore_jetpack.data.model.dto

data class Usage(
    val completion_tokens: Int = 0,
    val completion_tokens_details: CompletionTokensDetails = CompletionTokensDetails(),
    val prompt_tokens: Int = 0,
    val prompt_tokens_details: PromptTokensDetails = PromptTokensDetails(),
    val total_tokens: Int = 0
)