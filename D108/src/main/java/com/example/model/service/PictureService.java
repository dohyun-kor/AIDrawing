//C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\service\PictureService.java
package com.example.model.service;

import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureDto;
import com.example.model.dto.PictureUpdateRequestDto;

import java.util.List;

public interface PictureService {

    // 특정 사용자의 마이룸 그림 전체 조회
    List<PictureDto> getPicturesByUserId(int userId);

    // 마이룸 특정 그림 삭제
    void deletePictureById(int pictureId);

    // 전시 정보 업데이트 메서드 추가
    int updatePictureDisplay(int userId, List<PictureDisplayRequestDto> pictureDisplayRequestDtoList);

    // 그림 정보 수정 메서드
    int updatePictureInfo(PictureUpdateRequestDto pictureUpdateRequestDto);
}
