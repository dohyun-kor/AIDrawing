package com.example.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AssessmentLikesDao {
    /**
     * 좋아요 추가
     */
    int insertLike(@Param("assessmentId") int assessmentId, @Param("userId") int userId);

    /**
     * 좋아요 삭제
     */
    int deleteLike(@Param("assessmentId") int assessmentId, @Param("userId") int userId);

    /**
     * 특정 평가의 좋아요 개수 조회
     */
    int countLikes(@Param("assessmentId") int assessmentId);
}
