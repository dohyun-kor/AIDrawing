package com.example.controller;

import com.example.docs.DalleControllerDocs;
import com.example.model.dto.DalleResponseDto;
import com.example.model.service.DalleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DALLE", description = "DALLE 관련 API")
@RestController
@RequestMapping("/dalle")
public class DalleController implements DalleControllerDocs {

    private final DalleService dalleService;

    @Autowired
    public DalleController (DalleService dalleService) {
        this.dalleService = dalleService;
    }

    @PostMapping("/generate-image")
    public ResponseEntity<DalleResponseDto> generateImage(@RequestParam String subject, @RequestParam(required = false) String size) {
        // size가 null이거나 비어있으면 기본값 설정
        if (size == null || size.isEmpty()) {
            size = "1024x1024";  // 기본값 설정
        }

        try {
            DalleResponseDto dalleResponseDto = dalleService.generateImage(subject, size);
            return ResponseEntity.ok(dalleResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
