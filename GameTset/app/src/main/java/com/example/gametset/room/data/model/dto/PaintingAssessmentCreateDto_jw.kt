package com.example.gametset.room.data.model.dto

data class PaintingAssessmentCreateDto_jw(
    val userId: Int,
    val writerId: Int,
    val content: String,
    val score: Float,
    val pictureId: Int
) 