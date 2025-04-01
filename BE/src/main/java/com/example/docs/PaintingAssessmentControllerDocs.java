// 파일 경로: src/main/java/com/example/docs/PaintingAssessmentControllerDocs.java
package com.example.docs;

import com.example.model.dto.PaintingAssessmentDto;
import com.example.model.dto.PaintingAssessmentPutRequestDto;
import com.example.model.dto.PaintingAssessmentRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PaintingAssessmentControllerDocs {

    /**
     * 평가 작성 API
     * Request Body에는 작성할 평가의 userId, writer, content 등의 정보가 포함되어야 합니다.
     */
    @Operation(
            summary = "그림 평가 등록",
            description = "그림 평가를 등록합니다.. \n\n"
                    + "요청 본문에 작성할 평가 정보(userId, writer, content 등)를 포함시켜 요청합니다."
    )
    ResponseEntity<Boolean> createPaintingAssessment(@RequestBody PaintingAssessmentRequestDto paintingAssessmentRequestDto);

    /**
     * 평가 수정 API
     * Request Body에는 수정할 평가의 paintingAssessmentId 함께 수정할 내용(writer, content 등)이 포함되어야 합니다.
     */
    @Operation(
            summary = "그림 평가 수정",
            description = "그림 평가를 수정합니다..\n\n"
                    + "수정할 평가의 Id와 변경할 값(writer, content 등)을 요청 본문에 포함시켜 요청합니다."
    )
    ResponseEntity<Boolean> updatePaintingAssessment(@PathVariable int paintingAssessmentId,  @RequestBody PaintingAssessmentPutRequestDto paintingAssessmentPutRequestDto);

    /**
     * 평가 삭제 API
     * Query Parameter로 삭제할 평가의 paintingAssessmentId를 전달합니다.
     */
    @Operation(
            summary = "그림 평가 삭제",
            description = "평가를 삭제합니다.\n\n"
                    + "삭제할 평가의 paintingAssessmentId를 쿼리 파라미터로 전달합니다."
    )
    ResponseEntity<Boolean> deletePaintingAssessment(@PathVariable int paintingAssessmentId);
    /**
     * 사용자별 평가 조회 API
     * 특정 사용자의 전체 평가를 조회합니다.
     */
    @Operation(
            summary = "사용자별 그림 평가 조회",
            description = "특정 userId를 기준으로 전체 그림 평가를 조회합니다."
    )
    ResponseEntity<List<PaintingAssessmentDto>> getPaintingAssessmentsByUserId(@PathVariable int userId);

    /**
     * 그림별 평가 조회 API
     * 특정 그림에 대한 전체 평가를 조회합니다.
     */
    @Operation(
            summary = "그림별 평가 조회",
            description = "특정 pictureId를 기준으로 해당 그림의 전체 평가를 조회합니다."
    )
    ResponseEntity<List<PaintingAssessmentDto>> getPaintingAssessmentsByPictureId(@PathVariable int pictureId);

}
