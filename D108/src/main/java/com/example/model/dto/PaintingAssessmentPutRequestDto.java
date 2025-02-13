package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Setter
@Getter
@AllArgsConstructor
public class PaintingAssessmentPutRequestDto {
    private String content;
    private int score;
}
