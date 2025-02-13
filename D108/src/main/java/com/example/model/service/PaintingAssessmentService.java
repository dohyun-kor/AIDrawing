// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\service\PaintingAssessmentService.java
package com.example.model.service;

import com.example.model.dto.PaintingAssessmentDto;
import com.example.model.dto.PaintingAssessmentPutRequestDto;
import com.example.model.dto.PaintingAssessmentRequestDto;

import java.util.List;

public interface PaintingAssessmentService {
    /**
     * 그림 평가를 작성한다.
     * @param paintingAssessmentRequestDto 작성할 평가 데이터
     * @return 삽입된 행의 수 (정상 처리 시 1)
     */
    int createPaintingAssessment(PaintingAssessmentRequestDto paintingAssessmentRequestDto);

    /**
     * 평가 수정
     * @param paintingAssessmentPutRequestDto
     * @return 수정된 행의 수
     */
    int updatePaintingAssessment(int paintingAssessmentId, PaintingAssessmentPutRequestDto paintingAssessmentPutRequestDto);

    /**
     * 평가를 삭제한다.
     * @param PaintingAssessmentId 삭제할 평가의 고유 아이디
     * @return 삭제된 행의 수 (정상 처리 시 1)
     */
    int deletePaintingAssessment(int PaintingAssessmentId);

    /**
     * 사용자별 평가 조회
     * @param userId 조회할 사용자의 아이디
     * @return 해당 사용자의 평가 목록
     */
    List<PaintingAssessmentDto> getPaintingAssessmentsByUserId(int userId);

    /**
     * 그림별 평가 조회
     * @param pictureId 조회할 그림의 아이디
     * @return 해당 그림의 평가 목록
     */
    List<PaintingAssessmentDto> getPaintingAssessmentsByPictureId(int pictureId);
}
