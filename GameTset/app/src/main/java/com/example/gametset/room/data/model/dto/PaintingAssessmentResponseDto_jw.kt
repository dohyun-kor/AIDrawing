package com.example.gametset.room.data.model.dto

import com.google.gson.annotations.SerializedName

data class PaintingAssessmentResponseDto_jw(
    val paintingAssessmentId: Int,
    val userId: Int,
    val writerId: Int,
    val content: String,
    val score: Float,
    val pictureId: Int,
    val createdAt: String
) 