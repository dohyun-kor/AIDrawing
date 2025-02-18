// 파일 경로: src/main/java/com/example/model/dao/PictureDao.java
package com.example.model.dao;

import com.example.model.dto.PictureDto;
import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureUpdateRequestDto;
import com.example.model.dto.PictureUploadDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface PictureDao {
    // 특정 사용자의 마이룸 그림 전체 조회
    List<PictureDto> getPicturesByUserId(int userId);

    // 마이룸 특정 그림 삭제
    void deletePictureById(int pictureId);

    // 전시 정보 업데이트 (특정 그림)
    int updatePictureDisplay(int userId, PictureDisplayRequestDto pictureDisplayRequestDto);

    // 유저의 모든 그림을 초기화 (is_displayed = 0)
    int resetPictureDisplayForUser(int userId);

    int updatePictureInfo(int pictureId, PictureUpdateRequestDto pictureUpdateRequestDto);

    // 그림 db에 등록
    int uploadPicture(int userId, MultipartFile file, String topic);

    int insertNewPicture(PictureDto pictureDto);
}
