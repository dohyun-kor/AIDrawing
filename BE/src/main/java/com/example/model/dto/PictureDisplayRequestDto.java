package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PictureDisplayRequestDto {
    // 그림 고유 아이디 (picture_id)
    private int pictureId;
    // 회전 정도 (rotation)
    private int rotation;
    // 그림의 x 좌표 (x_val)
    private float xVal;
    // 그림의 y 좌표 (y_val)
    private float yVal;
    // 그림 액자
    private int furniture;
}
