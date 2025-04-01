package com.example.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PictureDto {
    private int pictureId;
    private int userId;
    private String imageUrl;
    private String topic;
    private String title;
    private String description;
    private boolean isDisplayed;
    private int rotation;
    private float xVal;
    private float yVal;
    private int furniture;
}
