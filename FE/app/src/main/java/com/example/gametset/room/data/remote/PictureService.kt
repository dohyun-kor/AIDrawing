package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.PictureDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.DELETE

interface PictureService {
    @GET("picture/{userId}")
    suspend fun getPicturesByUserId(@Path("userId") userId: Int): Response<List<PictureDto>>

    @POST("picture/{userId}")
    suspend fun savePictures(
        @Path("userId") userId: Int,
        @Body pictures: List<PictureDto>
    ): Response<Boolean>

    @DELETE("picture/{pictureId}")
    suspend fun deletePicture(
        @Path("pictureId") pictureId: Int
    ): Response<Void>
}