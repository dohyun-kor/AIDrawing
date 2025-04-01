package com.example.model.service;

import com.example.model.dao.PictureDao;
import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureDto;
import com.example.model.dto.PictureUpdateRequestDto;
import com.example.util.S3Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    private static final Logger logger = LoggerFactory.getLogger(PictureServiceImpl.class);

    private final PictureDao pictureDao;
    private final S3Uploader s3Uploader;

    @Autowired
    public PictureServiceImpl(PictureDao pictureDao, S3Uploader s3Uploader) {
        this.pictureDao = pictureDao;
        this.s3Uploader = s3Uploader;
    }

    // 특정 사용자의 마이룸 그림 전체 조회
    @Override
    public List<PictureDto> getPicturesByUserId(int userId) {
        logger.info("getPicturesByUserId - userId: {}", userId);
        return pictureDao.getPicturesByUserId(userId);
    }

    // 마이룸 특정 그림 삭제
    @Override
    public void deletePictureById(int pictureId) {
        logger.info("deletePictureById - pictureId: {}", pictureId);
        pictureDao.deletePictureById(pictureId);
    }

    // 전시 정보 업데이트 메서드 구현
    @Override
    public int updatePictureDisplay(int userId, List<PictureDisplayRequestDto> pictureDisplayRequestDtoList) {
        logger.info("updatePictureDisplay - userId: {}, request size: {}", userId,
                pictureDisplayRequestDtoList == null ? 0 : pictureDisplayRequestDtoList.size());

        // 1. 해당 유저의 모든 그림의 is_displayed 값을 초기화
        pictureDao.resetPictureDisplayForUser(userId);

        if (pictureDisplayRequestDtoList == null || pictureDisplayRequestDtoList.isEmpty()) {
            logger.warn("updatePictureDisplay - 요청받은 리스트가 비어있습니다.");
            return 0;
        }

        // 2. 요청받은 그림들에 대해 전시 정보를 업데이트 (is_displayed = 1)
        int successCount = 0;
        for (PictureDisplayRequestDto dto : pictureDisplayRequestDtoList) {
            int result = pictureDao.updatePictureDisplay(userId, dto);
            logger.debug("updatePictureDisplay - dto: {}, result: {}", dto, result);
            if (result == 1) {
                successCount++;
            }
        }
        logger.info("updatePictureDisplay - 성공한 업데이트 개수: {}", successCount);
        return successCount;
    }

    @Override
    public int updatePictureInfo(int pictureId, PictureUpdateRequestDto pictureUpdateRequestDto) {
        logger.info("updatePictureInfo - pictureId: {}, updateRequest: {}", pictureId, pictureUpdateRequestDto);
        return pictureDao.updatePictureInfo(pictureId, pictureUpdateRequestDto);
    }

    @Override
    public int uploadPicture(int userId, MultipartFile file, String topic) {
        logger.info("uploadPicture - userId: {}, topic: {}", userId, topic);
        try {
            // 1) MultipartFile → byte[]
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            logger.debug("uploadPicture - originalFilename: {}", originalFilename);
            String ext = "";
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 2) 유니크한 파일명 생성
            String uniqueFileName = "user_" + userId + "_" + System.currentTimeMillis() + ext;
            logger.debug("uploadPicture - uniqueFileName: {}", uniqueFileName);
            String contentType = file.getContentType() != null ? file.getContentType() : "image/png";
            logger.debug("uploadPicture - contentType: {}", contentType);

            // 3) S3 업로드 => URL 반환
            String s3Url = s3Uploader.uploadFile(fileBytes, uniqueFileName, contentType);
            logger.info("uploadPicture - S3 업로드 성공, URL: {}", s3Url);

            // 4) DB에 INSERT 위한 Dto 생성
            PictureDto picDto = new PictureDto();
            picDto.setUserId(userId);
            picDto.setImageUrl(s3Url);
            picDto.setTopic(topic);

            // 5) INSERT 수행
            int rowCount = pictureDao.insertNewPicture(picDto);
            logger.info("uploadPicture - DB INSERT 결과 rowCount: {}", rowCount);

            // rowCount == 1 이면 성공, picDto.getPictureId() 에 새로 생성된 PK가 세팅됨
            logger.info("uploadPicture - 최종 pictureId: {}", picDto.getPictureId());
            return picDto.getPictureId(); // pictureId 반환 (혹은 rowCount 반환)
        } catch (Exception e) {
            logger.error("uploadPicture - 예외 발생", e);
            return 0; // 실패 시 0 또는 -1 반환
        }
    }
}
