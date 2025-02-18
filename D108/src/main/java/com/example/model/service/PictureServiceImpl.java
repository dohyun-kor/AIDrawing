// 파일 경로: src/main/java/com/example/model/service/PictureServiceImpl.java
package com.example.model.service;

import com.example.model.dao.PictureDao;
import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureDto;
import com.example.model.dto.PictureUpdateRequestDto;
import com.example.util.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureDao pictureDao;
    private S3Uploader s3Uploader;

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
    public int updatePictureDisplay(int userId, List<PictureDisplayRequestDto> pictureDisplayRequestDtoList) {

        // 1. 해당 유저의 모든 그림의 is_displayed 값을 초기화
        pictureDao.resetPictureDisplayForUser(userId);

        if (pictureDisplayRequestDtoList == null || pictureDisplayRequestDtoList.isEmpty()) {
            return 0;
        }

        // 2. 요청받은 그림들에 대해 전시 정보를 업데이트 (is_displayed = 1)
        int successCount = 0;
        for (PictureDisplayRequestDto dto : pictureDisplayRequestDtoList) {
            int result = pictureDao.updatePictureDisplay(userId, dto);
            if (result == 1) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public int updatePictureInfo(int pictureId, PictureUpdateRequestDto pictureUpdateRequestDto) {
        return pictureDao.updatePictureInfo(pictureId, pictureUpdateRequestDto);
    }

    @Override
    public int uploadPicture(int userId, MultipartFile file, String topic) {
        try {
            // 1) MultipartFile → byte[]
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 2) 유니크한 파일명
            String uniqueFileName = "user_" + userId + "_" + System.currentTimeMillis() + ext;
            String contentType = file.getContentType() != null ? file.getContentType() : "image/png";

            // 3) S3 업로드 => URL 반환
            String s3Url = s3Uploader.uploadFile(fileBytes, uniqueFileName, contentType);

            // 4) DB에 INSERT Dto
            PictureDto picDto = new PictureDto();
            picDto.setUserId(userId);
            picDto.setImageUrl(s3Url);
            picDto.setTopic(topic);

            // 5) INSERT 수행
            int rowCount = pictureDao.insertNewPicture(picDto);

            // rowCount == 1 이면 성공
            // picDto.getPictureId() 에 새로 생성된 PK가 세팅됨
            return picDto.getPictureId(); // pictureId 반환 (혹은 rowCount 반환)
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // 실패 시 0 또는 -1
        }
    }
}
