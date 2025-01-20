package com.example.dalle.model;

import lombok.Data;

import java.util.List;

@Data
public class DalleResponse {
    private List<ImageData> data;

    @Data
    public static class ImageData {
        private String url; // URL of the generated image
    }
}
