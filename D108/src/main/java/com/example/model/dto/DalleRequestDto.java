package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
public class DalleRequestDto {

    private String prompt;

    private String size;    // Image size (e.g., 1024x1024)

    private int n;          // 생성할 이미지 개수

    // 생성자
    public DalleRequestDto(String subject, String size, int n) {
        this.prompt = "A quick, simple, sketch of a " + subject + " as if drawn by a person in 30 seconds, with minimal details and bold, easy-to-recognize shapes.";
        this.size = size;
        this.n = n;
    }
}