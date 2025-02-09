package com.example.controller;

import com.example.docs.MyRoomControllerDocs;
import com.example.model.dto.MyRoomDto;
import com.example.model.service.MyRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이룸", description = "마이룸 관련 API")
@RestController
@RequestMapping("/myroom")
public class MyRoomController implements MyRoomControllerDocs {

    private final MyRoomService myRoomService;

    @Autowired
    public MyRoomController(MyRoomService myRoomService) {
        this.myRoomService = myRoomService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MyRoomDto> getDisplayedItems(@PathVariable int userId) {
        try {
            MyRoomDto myRoomDto = myRoomService.getDisplayedItems(userId);
            return ResponseEntity.ok(myRoomDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
