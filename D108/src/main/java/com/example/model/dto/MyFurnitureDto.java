package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MyFurnitureDto {
    private int furId;

    public MyFurnitureDto(int furId) {
        this.furId = furId;
    }
}
