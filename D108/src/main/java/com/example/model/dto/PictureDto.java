package com.example.model.dto;

public class PictureDto {
    private int pictureId;
    private int userId;
    private String imageUrl;
    private String topic;
    private String title;
    private String description;
    private boolean isDisplayed;


    // 기본 생성자
    public PictureDto() {

    }

    public PictureDto(int pictureId, int userId, String imageUrl, String topic, String title, String description, boolean isDisplayed) {
        this.pictureId = pictureId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.topic = topic;
        this.title = title;
        this.description = description;
        this.isDisplayed = isDisplayed;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }
}
