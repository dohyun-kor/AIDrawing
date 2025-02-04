package com.example.controller;

import com.example.docs.UserControllerDocs;   // docs 인터페이스 임포트
import com.example.model.dto.User;
import com.example.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 최신 Spring Boot 버전을 기준으로 합니다.
@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController implements UserControllerDocs {   // docs 인터페이스 구현

    private final UserService userService;

    // 생성자 주입 사용 (최신 권장 방식)
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 API
     * 회원가입 시에는 닉네임, 아이디, 패스워드만 전달됩니다.
     */
    @PostMapping("/signup")
    @Override
    public ResponseEntity<Boolean> signup(@RequestBody User user) {
        int result = 0;
        try {
            // user 객체에는 nickname, id, password만 포함되어 있다고 가정합니다.
            result = userService.join(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result == 1) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 로그인 API
     * 로그인 시에는 아이디와 패스워드만 전달됩니다.
     */
    @PostMapping("/login")
    @Override
    public ResponseEntity<Integer> login(@RequestBody User user) {
        int result = -1;
        try {
            // user 객체에는 id와 password만 포함되어 있다고 가정합니다.
            result = userService.authenticate(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result != -1) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
