// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dao\PaintingAssessmentDao.java
package com.example.model.dao;


import com.example.model.dto.PaintingAssessmentDto;
import com.example.model.dto.PaintingAssessmentPutRequestDto;
import com.example.model.dto.PaintingAssessmentRequestDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaintingAssessmentDao {
    /**
     * 평가 작성
     * @param paintingAssessmentRequestDto, 작성할 평가 데이터
     * @return 삽입된 행의 수
     */
    int insertPaintingAssessment(PaintingAssessmentRequestDto paintingAssessmentRequestDto);

    /**
     * @param paintingAssessmentPutRequestDto, 수정할 평가 데이터
     * @return 수정한 행의 수
     */
    int updatePaintingAssessment(int paintingAssessmentId, PaintingAssessmentPutRequestDto paintingAssessmentPutRequestDto);

    /**
     * 평가 삭제
     * @param paintingAssessmentId, 삭제할 평가의 고유 아이디
     * @return 삭제된 행의 수
     */
    int deletePaintingAssessment(int paintingAssessmentId);

    /**
     * 사용자별 평가 조회
     * @param userId 평가를 조회할 사용자의 아이디
     * @return 해당 사용자의 평가 목록
     */
    List<PaintingAssessmentDto> selectPaintingAssessmentsByUserId(@Param("userId") int userId);

    /**
     * 그림별 평가 조회
     * @param pictureId 평가를 조회할 그림의 아이디
     * @return 해당 그림의 평가 목록
     */
    List<PaintingAssessmentDto> selectPaintingAssessmentsByPictureId(@Param("pictureId") int pictureId);

}

