package com.example.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PictureDto {
    private int pictureId;
    private int userId;
    private String imageUrl;
    private String topic;
    private String title;
    private String description;
    private boolean isDisplayed;


    public PictureDto(int pictureId, int userId, String imageUrl, String topic, String title, String description, boolean isDisplayed) {
        this.pictureId = pictureId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.topic = topic;
        this.title = title;
        this.description = description;
        this.isDisplayed = isDisplayed;
    }
}
