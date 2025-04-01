package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PictureUpdateRequestDto {
    // 수정할 제목
    private String title;
    // 수정할 설명
    private String description;
}
