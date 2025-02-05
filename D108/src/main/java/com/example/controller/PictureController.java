package com.example.controller;


import com.example.docs.PictureControllerDocs;
import com.example.model.dto.PictureDto;
import com.example.model.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/picture")
public class PictureController implements PictureControllerDocs {
    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    // 특정 사용자의 마이룸 그림 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<PictureDto>> getPicturesByUserId(@RequestParam int userId) {
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


}
