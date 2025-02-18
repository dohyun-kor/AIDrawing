package com.example.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PictureUploadDto {
    // 주제
    private String topic;
    // file?
    private String file;
}
