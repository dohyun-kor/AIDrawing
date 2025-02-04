package com.example.controller;

import com.example.model.dto.User;
import com.example.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @Autowired
    private UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody User user) {
        int result = 0;
        try{
            result = userService.join(user);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(result == 1){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 로그인 API (JWT 없이 성공/실패만 반환)
     * @param user (id, password)
     * @return statusCode와 message
     */
    @PostMapping("/login")
    public ResponseEntity<Integer> login(@RequestBody User user) {
        int result = -1;
        try {
            // 아이디/비밀번호 인증
            result = userService.authenticate(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != -1) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

