data class MyRoomItemDto(
    val itemId: Int,          // 액자의 ID
    val pictureId: Int? = null,      // 그림의 ID
    val pictureUrl: String? = null,  // 그림의 URL
    val xval: Float,          // x 위치
    val yval: Float,          // y 위치
    val rotation: Float = 0f,  // 회전 각도
    val frameUrl: String      // 액자 이미지 URL
) 