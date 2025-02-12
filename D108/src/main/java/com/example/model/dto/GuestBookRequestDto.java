package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GuestBookRequestDto {
    private int userId;
    private int writerId;
    private String content;
    private float score;

    public GuestBookRequestDto(int userId, int writerId, String content, float score) {
        this.userId = userId;
        this.writerId = writerId;
        this.content = content;
        this.score = score;
    }
}
