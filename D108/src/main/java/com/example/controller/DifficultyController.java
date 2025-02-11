package com.example.controller;


import com.example.docs.DifficultyControllerDocs;
import com.example.model.dto.DifficultyDto;
import com.example.model.service.DifficultyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "주제(단어)", description = "주제(단어) 관련 API")
@RestController
public class DifficultyController implements DifficultyControllerDocs {
    private final DifficultyService difficultyService;

    public DifficultyController(DifficultyService difficultyService) {
        this.difficultyService = difficultyService;
    }

    /**
     * 주어진 난이도에 맞는 2개의 주제를 반환하는 엔드포인트
     * @param difficulty 난이도 (easy, normal, hard)
     * @return 난이도에 맞는 2개의 주제 리스트
     */
    @GetMapping("/topics/{difficulty}")
    public ResponseEntity<List<Map<String, String>>> getTopicsByDifficulty(@PathVariable String difficulty) {
        try {
            // 주어진 난이도와 개수(2)를 이용해 주제 리스트를 반환
            List<DifficultyDto> topics = difficultyService.getTopicsByDifficulty(difficulty, 2);

            // 필요한 데이터만 추출하여 새로운 리스트 생성
            List<Map<String, String>> response = topics.stream()
                    .map(topic -> Map.of("topic", topic.getTopic()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
