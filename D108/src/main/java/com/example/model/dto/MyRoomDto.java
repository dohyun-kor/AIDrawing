package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MyRoomDto {

    // 전시된 그림 리스트
    public List<PictureDto> displayedPictures;

    public MyRoomDto(List<PictureDto> displayedPictures) {
        this.displayedPictures = displayedPictures;
    }
}
