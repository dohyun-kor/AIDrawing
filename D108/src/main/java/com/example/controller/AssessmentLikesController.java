package com.example.controller;

import com.example.model.service.AssessmentLikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "평가 - 좋아요", description = "평가에 좋아요 및 취소, 개수 확인")
@RestController
@RequestMapping("/assessmentLikes")
public class AssessmentLikesController {

    private final AssessmentLikesService assessmentLikesService;

    @Autowired
    public AssessmentLikesController(AssessmentLikesService assessmentLikesService) {
        this.assessmentLikesService = assessmentLikesService;
    }

    @Operation(summary = "좋아요 추가", description = "해당 평가에 좋아요를 추가합니다.")
    @PostMapping("/{assessmentId}/like/{userId}")
    public ResponseEntity<Boolean> addLike(@PathVariable int assessmentId, @PathVariable int userId) {
        boolean result = assessmentLikesService.addLike(assessmentId, userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좋아요 삭제", description = "해당 평가에서 좋아요를 취소합니다.")
    @DeleteMapping("/{assessmentId}/like/{userId}")
    public ResponseEntity<Boolean> removeLike(@PathVariable int assessmentId, @PathVariable int userId) {
        boolean result = assessmentLikesService.removeLike(assessmentId, userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좋아요 수 조회", description = "해당 평가의 좋아요 개수를 조회합니다.")
    @GetMapping("/{assessmentId}/likes")
    public ResponseEntity<Integer> getLikeCount(@PathVariable int assessmentId) {
        int count = assessmentLikesService.getLikeCount(assessmentId);
        return ResponseEntity.ok(count);
    }
}
