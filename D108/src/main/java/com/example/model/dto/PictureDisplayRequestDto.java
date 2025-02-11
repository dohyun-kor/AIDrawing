// 파일 경로: src/main/java/com/example/model/dto/PictureDto.java
package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PictureDisplayRequestDto {
    // 그림 고유 아이디 (picture_id)
    private int pictureId;
    // 회전 정도 (rotation)
    private int rotation;
    // 그림의 x 좌표 (x_val)
    private float xVal;
    // 그림의 y 좌표 (y_val)
    private float yVal;

    // 필요에 따른 생성자
    public PictureDisplayRequestDto(int pictureId, int rotation, float xVal, float yVal) {
        this.pictureId = pictureId;
        this.rotation = rotation;
        this.xVal = xVal;
        this.yVal = yVal;
    }
}
