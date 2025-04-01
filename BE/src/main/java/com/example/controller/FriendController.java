package com.example.controller;

import com.example.docs.FriendControllerDocs;
import com.example.model.dto.FriendDto;
import com.example.model.dto.FriendRequestDto;
import com.example.model.service.FriendService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "친구", description = "친구 관련 API")
@RestController
@RequestMapping("/friend")
public class FriendController implements FriendControllerDocs{

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @Autowired
    FriendService fService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendDto>> searchFriend(@PathVariable int userId) {
        try {
            List<FriendDto> friendDtoList = fService.searchFriends(userId);
            return ResponseEntity.ok(friendDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("")
    public ResponseEntity<Boolean> requestFriends(@RequestBody FriendRequestDto friendRequestDto){
        int result = 0;
        try{
            result = fService.requestFriends(friendRequestDto);
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(result == 1){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("")
    public ResponseEntity<Boolean> requestUpdate(@RequestBody FriendDto nFriendDto){
        int result = 0;
        try{
            result = fService.updateRequest(nFriendDto);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(result == 1){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
