package com.example.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
public class DalleResponseDto {
    private List<ImageData> data;

    // ImageData 클래스는 URL을 저장하는 객체이다.
    @Data
    public static class ImageData {
        private String url;
    }

}
