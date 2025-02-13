// 파일 경로: src/main/java/com/example/controller/PaintingAssessmentController.java
package com.example.controller;

import com.example.docs.PaintingAssessmentControllerDocs;  // Swagger 문서용 인터페이스 (필요시)
import com.example.model.dto.PaintingAssessmentDto;
import com.example.model.dto.PaintingAssessmentPutRequestDto;
import com.example.model.dto.PaintingAssessmentRequestDto;
import com.example.model.service.PaintingAssessmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "평가", description = "평가 작성 및 삭제 관련 API")
@RestController
@RequestMapping("/paintingAssessment")
public class PaintingAssessmentController implements PaintingAssessmentControllerDocs {

    private final PaintingAssessmentService paintingAssessmentService;

    @Autowired
    public PaintingAssessmentController(PaintingAssessmentService paintingAssessmentService) {
        this.paintingAssessmentService = paintingAssessmentService;
    }

    /**
     * 평가 작성 API
     * URL: POST /PaintingAssessment
     * @param paintingAssessmentRequestDto 작성할 평가 데이터 (userId, writer, content 등 포함)
     * @return 작성 성공 시 true, 실패 시 500 에러 반환
     */
    @PostMapping
    public ResponseEntity<Boolean> createPaintingAssessment(@RequestBody PaintingAssessmentRequestDto paintingAssessmentRequestDto) {
        try {
            int result = paintingAssessmentService.createPaintingAssessment(paintingAssessmentRequestDto);
            if (result == 1) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**``
     * 평가 수정 api
     * @param paintingAssessmentPutRequestDto
     * @return
     */
    @PutMapping("/{paintingAssessmentId}")
    public ResponseEntity<Boolean> updatePaintingAssessment(@PathVariable int paintingAssessmentId, @RequestBody PaintingAssessmentPutRequestDto paintingAssessmentPutRequestDto) {
        try {
            int result = paintingAssessmentService.updatePaintingAssessment(paintingAssessmentId, paintingAssessmentPutRequestDto);
            if (result == 1) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * 평가 삭제 API
     * URL: DELETE /PaintingAssessment?PaintingAssessmentId={PaintingAssessmentId}
     * @param paintingAssessmentId 삭제할 평가의 고유 아이디
     * @return 삭제 성공 시 true, 실패 시 500 에러 반환
     */
    @DeleteMapping("/{paintingAssessmentId}")
    public ResponseEntity<Boolean> deletePaintingAssessment(@PathVariable int paintingAssessmentId) {
        try {
            int result = paintingAssessmentService.deletePaintingAssessment(paintingAssessmentId);
            if (result == 1) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용자별 평가 조회 API
     * URL: GET /paintingAssessment/user/{userId}
     * @param userId 평가를 조회할 사용자의 고유 아이디
     * @return 해당 사용자의 평가 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaintingAssessmentDto>> getPaintingAssessmentsByUserId(@PathVariable int userId) {
        try {
            List<PaintingAssessmentDto> assessments = paintingAssessmentService.getPaintingAssessmentsByUserId(userId);
            return ResponseEntity.ok(assessments);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 그림별 평가 조회 API
     * URL: GET /paintingAssessment/picture/{pictureId}
     * @param pictureId 평가를 조회할 그림의 고유 아이디
     * @return 해당 그림에 대한 평가 목록
     */
    @GetMapping("/picture/{pictureId}")
    public ResponseEntity<List<PaintingAssessmentDto>> getPaintingAssessmentsByPictureId(@PathVariable int pictureId) {
        try {
            List<PaintingAssessmentDto> assessments = paintingAssessmentService.getPaintingAssessmentsByPictureId(pictureId);
            return ResponseEntity.ok(assessments);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
