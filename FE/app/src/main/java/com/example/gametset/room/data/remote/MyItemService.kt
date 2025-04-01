package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.MyItemDto
import com.example.gametset.room.data.model.dto.PurchaseRequest
import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.data.model.response.PurchaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyItemService {

    @GET("myItems")
    suspend fun myItemsList(@Query("userId") userId: Int): List<MyItemDto>

    @POST("myItems")
    suspend fun purchase(@Body purchaseRequest: PurchaseRequest): Boolean


}