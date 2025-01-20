package com.example.dalle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.dalle.model.DalleRequest;
import com.example.dalle.model.DalleResponse;

@Service
public class DalleService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // RestTemplate을 의존성 주입받는 방식
    public DalleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DalleResponse generateImage(DalleRequest request) {
        String url = "https://api.openai.com/v1/images/generations";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<DalleRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.getMessageConverters().forEach(converter -> {
            System.out.println("Converter: " + converter.getClass().getName());
        });


        ResponseEntity<DalleResponse> response = restTemplate.postForEntity(url, entity, DalleResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("DALL·E API 호출 실패: " + response.getStatusCode());
        }
    }
}
