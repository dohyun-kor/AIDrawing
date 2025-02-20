package com.example.gametset.room.data.model.dto

data class GameUserInfo(
    var userId: Int,
    var score: Int,
    var isCorrect: Boolean,
    var isHostId: Boolean,
)