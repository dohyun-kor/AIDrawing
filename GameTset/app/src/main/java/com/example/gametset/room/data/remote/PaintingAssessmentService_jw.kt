package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.PaintingAssessmentCreateDto_jw
import com.example.gametset.room.data.model.dto.PaintingAssessmentResponseDto_jw
import com.example.gametset.room.data.model.dto.PaintingAssessmentUpdateDto_jw
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaintingAssessmentService_jw {
    @PUT("paintingAssessment/{paintingAssessmentId}")
    suspend fun updatePaintingAssessment(
        @Path("paintingAssessmentId") paintingAssessmentId: Int,
        @Body assessmentUpdate: PaintingAssessmentUpdateDto_jw
    ): Boolean

    @DELETE("paintingAssessment/{paintingAssessmentId}")
    suspend fun deletePaintingAssessment(
        @Path("paintingAssessmentId") paintingAssessmentId: Int
    ): Boolean

    @POST("paintingAssessment")
    suspend fun createPaintingAssessment(
        @Body assessment: PaintingAssessmentCreateDto_jw
    ): Boolean

    @GET("paintingAssessment/user/{userId}")
    suspend fun getPaintingAssessmentsByUserId(
        @Path("userId") userId: Int
    ): List<PaintingAssessmentResponseDto_jw>

    @GET("paintingAssessment/picture/{pictureId}")
    suspend fun getPaintingAssessmentsByPictureId(
        @Path("pictureId") pictureId: Int
    ): List<PaintingAssessmentResponseDto_jw>
} 