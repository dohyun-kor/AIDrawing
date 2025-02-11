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
        this.prompt = "A simple and cute black-and-white line drawing of a " + subject + ", designed with clean and minimal outlines, resembling a hand-drawn sketch with a playful and adorable cartoon style. The design should look like a casual sticker or a fun illustration for social media.";
        this.size = size;
        this.n = n;
    }
}