package com.example.controller;

import com.example.docs.FriendControllerDocs;
import com.example.model.dto.Friend;
import com.example.model.dto.FriendRequest;
import com.example.model.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController implements FriendControllerDocs{

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @Autowired
    FriendService fService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Friend>> searchFriend(@PathVariable int userId) {
        try {
            List<Friend> friendList = fService.searchFriends(userId);
            return ResponseEntity.ok(friendList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("")
    public ResponseEntity<Boolean> requestFriends(@RequestBody FriendRequest friendRequest){
        int result = 0;
        try{
            result = fService.requestFriends(friendRequest);
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
    public ResponseEntity<Boolean> requestUpdate(@RequestBody Friend nFriend){
        int result = 0;
        try{
            result = fService.updateRequest(nFriend);
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
