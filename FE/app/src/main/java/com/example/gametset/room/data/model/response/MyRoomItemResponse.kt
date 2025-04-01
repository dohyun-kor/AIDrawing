package com.example.gametset.room.data.model.response

import android.graphics.PointF
import com.example.gametset.room.data.model.ItemInfo
import com.example.gametset.room.data.model.MyRoomItem

data class MyRoomItemResponse(
    val pictureId: Int,
    val imageUrl: String,
    val rotation: Int,
    val furniture: Int,
    val xval: Float,
    val yval: Float,
//    val itemInfo: ItemInfo
) {
    fun toMyRoomItem(): MyRoomItem {
        return MyRoomItem(
            id = pictureId,
            type = if (furniture > 0) "FRAME" else "PICTURE",
            imageUrl = imageUrl,
            position = PointF(xval, yval),
            rotation = rotation.toFloat(),
//            info = itemInfo
        )
    }
}

fun MyRoomItem.toResponse(): MyRoomItemResponse {
    return MyRoomItemResponse(
        pictureId = id,
        imageUrl = imageUrl,
        rotation = rotation.toInt(),
        furniture = if (type == "FRAME") 1 else 0,
        xval = position.x,
        yval = position.y,
//        itemInfo = info
    )
}










