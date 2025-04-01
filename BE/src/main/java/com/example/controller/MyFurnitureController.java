package com.example.controller;

import com.example.docs.MyFurnitureControllerDocs;
import com.example.model.dto.MyFurnitureDto;
import com.example.model.service.MyFurnitureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "가구", description = "가구 관련 API")
@RestController
@RequestMapping("/myfurniture")
public class MyFurnitureController implements MyFurnitureControllerDocs {

    private final MyFurnitureService myFurnitureService;

    @Autowired
    public MyFurnitureController(MyFurnitureService myFurnitureService) {
        this.myFurnitureService = myFurnitureService;
    }

    // 가구 전시 요청 처리
    @PostMapping("/{userId}")
    public ResponseEntity<Boolean> displayFurniture(@PathVariable int userId, @RequestBody MyFurnitureDto myFurnitureDto) {
        int result = 0;
        try {
            result = myFurnitureService.displayFurniture(userId, myFurnitureDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result == 1) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
