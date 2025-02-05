package com.example.model.dao;

import com.example.model.dto.PictureDto;

import java.util.List;

public interface PictureDao {
    //특정 사용자의 마이룸 그림 전체 조회
    List<PictureDto> getPicturesByUserId(int userId);

    // 마이룸 특정 그림 삭제
    void deletePictureById(int pictureId);

}
