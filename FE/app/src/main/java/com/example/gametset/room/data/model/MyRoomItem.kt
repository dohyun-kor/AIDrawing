package com.example.gametset.room.data.model

import android.graphics.PointF
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.gametset.room.data.model.ItemInfo
import android.view.View
import com.example.gametset.room.data.model.dto.PictureDto
import com.example.gametset.room.data.model.response.PictureDisplayRequestDto

data class MyRoomItem(
    val id: Int,                // 액자의 ID
    val type: String,           // 아이템 타입 (FRAME, PICTURE 등)
    var imageUrl: String,       // 액자 이미지 URL
    var position: PointF = PointF(0f, 0f),  // x, y 위치
    var rotation: Float = 0f,   // 회전 각도
    var pictureId: Int? = null, // 그림 ID (없을 수 있음)
    var pictureUrl: String? = null,  // 그림 URL (없을 수 있음)
    var topic: String? = null   // topic 필드 추가
) {
    companion object {
        fun toJson(items: List<MyRoomItem>): String {
            return Gson().toJson(items)
        }

        fun fromJson(json: String): List<MyRoomItem> {
            val type = object : TypeToken<List<MyRoomItem>>() {}.type
            return try {
                Gson().fromJson(json, type) ?: listOf()
            } catch (e: Exception) {
                listOf()
            }
        }

        fun fromPictureDto(pictureDto: PictureDto, frameUrl: String) = MyRoomItem(
            id = pictureDto.furniture,
            type = "FRAME",
            imageUrl = frameUrl,
            position = PointF(pictureDto.xval, pictureDto.yval),
            rotation = pictureDto.rotation.toFloat(),
            pictureId = pictureDto.pictureId,
            pictureUrl = pictureDto.imageUrl,
            topic = pictureDto.topic  // topic 정보 추가
        )
    }

    // 서버 요청용 변환 메서드
    fun toDisplayRequest() = PictureDisplayRequestDto(
        pictureId = pictureId ?: id,  // pictureId가 있으면 사용, 없으면 id 사용
        rotation = rotation.toInt(),
        furniture = id,  // 액자 ID
        x_val = position.x,
        y_val = position.y
    )
} 