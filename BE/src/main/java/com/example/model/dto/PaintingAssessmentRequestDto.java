package com.example.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PaintingAssessmentRequestDto {
    private int userId;
    private int writerId;
    private String content;
    private float score;
    private int pictureId;
}
