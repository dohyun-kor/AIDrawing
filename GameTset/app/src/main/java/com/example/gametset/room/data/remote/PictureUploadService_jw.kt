package com.example.gametset.room.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PictureUploadService_jw {
    @Multipart
    @POST("picture/upload/{userId}")
    suspend fun uploadPicture(
        @Path("userId") userId: Int,
        @Query("topic") topic: String,
        @Part file: MultipartBody.Part
    ): Response<Boolean>  // pictureId를 반환
} 