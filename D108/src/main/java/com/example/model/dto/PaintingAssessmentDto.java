package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaintingAssessmentDto {

    // 평가 고유 아이디
    private int paintingAssessmentId;
    // 마이룸 소유자 사용자 아이디
    private int userId;
    // 평가 작성자 아이디
    private int writerId;
    // 평가 내용
    private String content;
    // 별점
    private float score;
    // 그림 아이디
    private int pictureId;
    // 시간
    private LocalDateTime createdAt;

}
