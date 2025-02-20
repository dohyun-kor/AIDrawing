package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.PictureDto
import com.example.gametset.room.data.model.response.PictureDisplayRequestDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.DELETE

interface MyRoomService {
    @GET("myroom/{userId}")
    suspend fun getMyRoomItems(@Path("userId") userId: Int): List<PictureDto>

    @POST("myroom/{userId}")
    @Headers("Content-Type: application/json")
    suspend fun saveMyRoomItems(
        @Path("userId") userId: Int,
        @Body items: List<PictureDisplayRequestDto>
    ): Response<Unit>

    @POST("picture/{userId}")
    suspend fun savePictureDisplay(
        @Path("userId") userId: Int,
        @Body displayRequests: List<PictureDisplayRequestDto>
    ): Response<Boolean>

    @DELETE("myroom/{userId}/items/{itemId}")
    suspend fun deleteMyRoomItem(
        @Path("userId") userId: Int,
        @Path("itemId") itemId: Int
    ): Response<Unit>
} 