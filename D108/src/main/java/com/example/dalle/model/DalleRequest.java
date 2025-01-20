package com.example.dalle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DalleRequest {
    @JsonProperty("prompt")
    private String prompt;
    @JsonProperty("n")
    private int n; // Number of images
    @JsonProperty("size")
    private String size; // Image size (e.g., 1024x1024)
}
