package com.example.gametset.ranking.data.remote

import com.example.gametset.ranking.data.model.dto.UserRankingDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RankingService {
    @GET("ranking/{userId}")
    suspend fun getUserRanking(@Path("userId") userId: Int): UserRankingDto

    @GET("ranking/list")
    suspend fun getRankingList(): List<UserRankingDto>
}