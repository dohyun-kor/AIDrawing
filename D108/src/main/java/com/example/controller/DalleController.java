package com.example.controller;

import com.example.docs.DalleControllerDocs;
import com.example.model.dto.DalleRequestDto;
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
     * @return 생성된 이미지 리스트
     */
    @PostMapping("/generate-image")
    public ResponseEntity<DalleResponseDto> generateImage(
            @RequestParam String difficulty,
            @RequestParam(required = false) String size) {

        // size가 null이거나 비어있으면 기본값 설정
        if (size == null || size.isEmpty()) {
            size = "1024x1024";  // 기본값 설정
        }

        try {
            // 난이도에 따라 단일 주제를 가져옴
            List<DifficultyDto> topics = difficultyService.getTopicsByDifficulty(difficulty, 1);

            // 첫 번째 주제를 이용해 이미지를 생성
            String subject = topics.get(0).getTopicEn();
            System.out.println("subject : " + topics.get(0).getTopic() + subject);
            // DALL·E API 호출
            DalleResponseDto response = dalleService.generateImage(subject, size, 1);

            return ResponseEntity.ok(response); // 성공적으로 이미지 반환

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
