// 파일 경로: src/main/java/com/example/model/service/PictureServiceImpl.java
package com.example.model.service;

import com.example.model.dao.PictureDao;
import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureDao pictureDao;

    @Autowired
    public PictureServiceImpl(PictureDao pictureDao) {
        this.pictureDao = pictureDao;
    }

    // 특정 사용자의 마이룸 그림 전체 조회
    @Override
    public List<PictureDto> getPicturesByUserId(int userId) {
        return pictureDao.getPicturesByUserId(userId);
    }

    // 마이룸 특정 그림 삭제
    @Override
    public void deletePictureById(int pictureId) {
        pictureDao.deletePictureById(pictureId);
    }

    // 전시 정보 업데이트 메서드 구현
    @Override
    public int updatePictureDisplay(List<PictureDisplayRequestDto> pictureDisplayRequestDtoList) {
        if (pictureDisplayRequestDtoList == null || pictureDisplayRequestDtoList.isEmpty()) {
            return 0;
        }
        // 첫 번째 picture_id로부터 userId 추출
        int firstPictureId = pictureDisplayRequestDtoList.get(0).getPictureId();
        PictureDto pictureInfo = pictureDao.getPictureById(firstPictureId);
        if(pictureInfo == null) {
            throw new RuntimeException("유효하지 않은 pictureId: " + firstPictureId);
        }
        int userId = pictureInfo.getUserId();

        // 추가 검증: 모든 DTO가 같은 userId에 해당하는지 검사 (옵션)
        for (PictureDisplayRequestDto dto : pictureDisplayRequestDtoList) {
            PictureDto info = pictureDao.getPictureById(dto.getPictureId());
            if (info == null || info.getUserId() != userId) {
                throw new RuntimeException("모든 pictureId는 동일한 사용자에 속해야 합니다.");
            }
        }

        // 1. 해당 유저의 모든 그림의 is_displayed 값을 초기화
        pictureDao.resetPictureDisplayForUser(userId);

        // 2. 요청받은 그림들에 대해 전시 정보를 업데이트 (is_displayed = 1)
        int successCount = 0;
        for (PictureDisplayRequestDto dto : pictureDisplayRequestDtoList) {
            int result = pictureDao.updatePictureDisplay(dto);
            if (result == 1) {
                successCount++;
            }
        }
        return successCount;
    }
}
