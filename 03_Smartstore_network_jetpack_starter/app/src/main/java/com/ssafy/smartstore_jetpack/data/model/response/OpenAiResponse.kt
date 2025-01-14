package com.ssafy.smartstore_jetpack.data.model.response

import com.ssafy.smartstore_jetpack.data.model.dto.Choice
import com.ssafy.smartstore_jetpack.data.model.dto.Usage

data class OpenAiResponse(
    var choices: List<Choice> = listOf(Choice()),
    var created: Int = 0,
    var id: String = "",
    var model: String = "gpt-4o-mini",
    var `object`: String = "",
    var system_fingerprint: String ="",
    var usage: Usage = Usage()
)