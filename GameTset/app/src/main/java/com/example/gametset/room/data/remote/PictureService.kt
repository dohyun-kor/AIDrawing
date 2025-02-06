package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.PictureDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureService {
    @GET("picture/list")
    fun getPictureDtoList(@Query("userId") userId: Int): List<PictureDto>
}