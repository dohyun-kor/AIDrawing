package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
public class DalleRequestDto {

    private String prompt;

    private String size;    // Image size (e.g., 1024x1024)

    // 생성자
    public DalleRequestDto(String subject, String size) {
        this.prompt = "A simple pencil drawing of a " + subject + ", with soft touches of color, resembling the sketchy, playful style seen in games like Gartic Phone.";

        this.size = size;
    }
}