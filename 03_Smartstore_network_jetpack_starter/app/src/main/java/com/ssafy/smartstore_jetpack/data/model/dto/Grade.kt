package com.ssafy.smartstore_jetpack.data.model.dto

data class Grade(
    var title: String,
    var img: String,
    var step: Int,
    var stepMax: Int,
    var to: Int,
)