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

    // 전시된 가구 리스트
    public List<MyFurnitureDto> displayedFurniture;

    public MyRoomDto(List<PictureDto> displayedPictures, List<MyFurnitureDto> displayedFurniture) {
        this.displayedPictures = displayedPictures;
        this.displayedFurniture = displayedFurniture;
    }
}
