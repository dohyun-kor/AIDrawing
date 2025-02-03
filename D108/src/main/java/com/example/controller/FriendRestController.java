package com.example.controller;

import com.example.docs.FriendControllerDocs;
import com.example.model.dto.Friend;
import com.example.model.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@CrossOrigin("*")
public class FriendRestController implements FriendControllerDocs{

    private static final Logger logger = LoggerFactory.getLogger(FriendRestController.class);

    @Autowired
    FriendService fService;

    @GetMapping("/list")
    public ResponseEntity<List<Friend>> searchFriend(@RequestParam int userId) {
        try {
            List<Friend> friendList = fService.searchFriends(userId);
            logger.info("친구정보 : {}",friendList);
            return ResponseEntity.ok(friendList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
