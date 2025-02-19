// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\controller\PictureController.java
package com.example.controller;


import com.example.docs.PictureControllerDocs;
import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureDto;
import com.example.model.dto.PictureUpdateRequestDto;
import com.example.model.service.PictureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "그림(사진)", description = "그림(사진) 관련 API")
@RestController
@RequestMapping("/picture")
public class PictureController implements PictureControllerDocs {
    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    // 특정 사용자의 마이룸 그림 전체 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<PictureDto>> getPicturesByUserId(@PathVariable int userId) {
        try {
            List<PictureDto> pictures = pictureService.getPicturesByUserId(userId);
            return ResponseEntity.ok(pictures);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{pictureId}")
    public void deletePicture(@PathVariable int pictureId) {
        pictureService.deletePictureById(pictureId);
    }

    /**
     * 해당 사용자(userId)의 마이룸에 전달받은 그림 전시 정보를 업데이트한다.
     *
     * @param pictureDisplayRequestDtoList  전시할 그림 정보 리스트 (요청 본문)
     * @return 모든 업데이트가 성공하면 true, 아니면 500 에러 반환
     */
    // 전시 정보 업데이트 API 추가
    @PostMapping("/{userId}")
    public ResponseEntity<Boolean> updatePictureDisplay(@PathVariable int userId, @RequestBody List<PictureDisplayRequestDto> pictureDisplayRequestDtoList) {
        int result = 0;
        try {
            result = pictureService.updatePictureDisplay(userId, pictureDisplayRequestDtoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 모든 그림 정보가 정상 업데이트 되었을 경우 성공 응답
        if(result == pictureDisplayRequestDtoList.size()) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 그림의 제목과 설명을 수정하는 API
     *
     * @param pictureUpdateRequestDto 수정할 그림 정보
     * @return 수정이 성공하면 true, 실패 시 500 에러 응답
     */
    @PutMapping("/{pictureId}")
    public ResponseEntity<Boolean> updatePictureInfo(@PathVariable int pictureId, @RequestBody PictureUpdateRequestDto pictureUpdateRequestDto) {
        try {
            int result = pictureService.updatePictureInfo(pictureId, pictureUpdateRequestDto);
            if(result == 1){
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }   catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> uploadPicture(
            @PathVariable int userId,                   // URL 경로의 userId 파라미터
            @RequestParam("file") MultipartFile file,    // 업로드할 파일
            @RequestParam("topic") String topic) {       // 추가적인 topic 정보
        try {
            // pictureService.uploadPicture()를 호출하여 업로드 수행
            // 성공하면 pictureId가 0보다 큰 값이 리턴됩니다.
            int pictureId = pictureService.uploadPicture(userId, file, topic);

            // 업로드 성공 여부에 따라 true 또는 false를 리턴합니다.
            return ResponseEntity.ok(pictureId > 0);
        } catch (Exception e) {
            // 예외 발생 시 콘솔에 예외 정보를 출력하고 false를 리턴합니다.
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }
    }
}
