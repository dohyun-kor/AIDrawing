package com.example.controller;

import com.example.docs.DalleControllerDocs;
import com.example.model.dto.DalleResponseDto;
import com.example.model.dto.DifficultyDto;
import com.example.model.service.DalleService;
import com.example.model.service.DifficultyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "DALLE", description = "DALLE 관련 API")
@RestController
@RequestMapping("/dalle")
public class DalleController implements DalleControllerDocs {

    private final DalleService dalleService;
    private final DifficultyService difficultyService;

    @Autowired
    public DalleController (DalleService dalleService, DifficultyService difficultyService) {
        this.dalleService = dalleService;
        this.difficultyService = difficultyService;
    }

    /**
     * DALL·E API를 호출하여 이미지를 생성합니다.
     * @param difficulty 난이도 (easy, normal, hard)
     * @param count 가져올 주제 개수
     * @param size 이미지 크기 (기본값: 1024x1024)
     * @return 생성된 이미지 리스트
     */
    @PostMapping("/generate-image")
    public ResponseEntity<DalleResponseDto> generateImage(
            @RequestParam String difficulty,
            @RequestParam int count,
            @RequestParam(required = false) String size) {

        // size가 null이거나 비어있으면 기본값 설정
        if (size == null || size.isEmpty()) {
            size = "1024x1024";  // 기본값 설정
        }

        try {
            // 난이도에 맞는 주제를 조회
            List<DifficultyDto> topics = difficultyService.getTopicsByDifficulty(difficulty, count);

            // 여러 개의 이미지 URL을 생성하여 리스트에 추가
            List<String> imageUrls = new ArrayList<>();

            // 각 주제에 대해 이미지 생성 요청
            for (DifficultyDto topic : topics) {
                String subject = topic.getTopic();

                // topic_en 출력 (디버깅용)
                System.out.println("Topic in English: " + subject);

                // 이미지 생성 요청
                DalleResponseDto dalleResponseDto = dalleService.generateImage(subject, size, 1);

                // 여러 이미지를 한 번에 반환
                for (DalleResponseDto.ImageData image : dalleResponseDto.getData()) {
                    imageUrls.add(image.getUrl());
                }
            }

            // 최종 이미지 URL 목록을 담은 DalleResponseDto 반환
            DalleResponseDto responseDto = new DalleResponseDto();
            List<DalleResponseDto.ImageData> imageDataList = new ArrayList<>();
            for (String url : imageUrls) {
                DalleResponseDto.ImageData imageData = new DalleResponseDto.ImageData();
                imageData.setUrl(url);
                imageDataList.add(imageData);
            }
            responseDto.setData(imageDataList);

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
