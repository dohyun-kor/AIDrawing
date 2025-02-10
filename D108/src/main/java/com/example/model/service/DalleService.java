package com.example.model.service;

import com.example.model.dto.DalleRequestDto;
import com.example.model.dto.DalleResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DalleService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public DalleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * DALL·E API 호출 메서드
     * @param subject 단어
     * @param size 이미지 크기 (e.g., 1024x1024)
     * @return DalleResponseDto 응답 객체
     */
    public DalleResponseDto generateImage(String subject, String size) {
        DalleRequestDto dalleRequestDto = new DalleRequestDto(subject, size);

        String url = "https://api.openai.com/v1/images/generations";

        System.out.println("API Key: " + apiKey); // 디버깅용

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // 요청 본문 생성
        HttpEntity<DalleRequestDto> entity = new HttpEntity<>(dalleRequestDto, headers);

        // API 호출
        ResponseEntity<DalleResponseDto> response = restTemplate.postForEntity(url, entity, DalleResponseDto.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            throw new RuntimeException("DALL·E API 호출 실패: " + response.getStatusCode());
        }
    }
}
