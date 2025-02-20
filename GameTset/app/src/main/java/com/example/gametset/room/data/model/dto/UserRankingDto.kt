package com.example.gametset.ranking.data.model.dto

data class UserRankingDto(
    val userId: Int,
    val exp: Int,
    val nickname: String,
    val rankPosition: Int,
    val winRate: Int
)