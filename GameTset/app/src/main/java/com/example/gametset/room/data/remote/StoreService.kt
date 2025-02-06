package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.StoreDto
import retrofit2.http.GET

interface StoreService {

    // 아이템 전체 조회
    @GET("item")
    suspend fun getItem() :List<StoreDto>

}