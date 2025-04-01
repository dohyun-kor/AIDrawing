package com.example.gametset.ranking.data.remote

import com.example.gametset.ranking.data.model.dto.TopRankingListDto
import retrofit2.http.GET

interface TopRankingService {
    @GET("ranking/list")
    suspend fun getRankingList(): List<TopRankingListDto>
}