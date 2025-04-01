package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.StoreDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreService {

    // 아이템 전체 조회
    @GET("items")
    suspend fun getItem(): List<StoreDto>

    @GET("items/{itemId}")
    suspend fun getOneItem(@Path("itemId") itemId:Int): StoreDto

} 