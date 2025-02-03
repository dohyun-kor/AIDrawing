package com.example.controller;

import com.example.model.dto.User;
import com.example.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, Object> signup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 회원가입 처리
            int result = userService.join(user);

            if (result == 1) {

//                // 회원가입 성공 시 JWT 토큰 생성
//                String token = JWTUtil.generateToken(user.getId());
                response.put("statusCode", 200);
                response.put("message", "회원가입 되었습니다.");
//                response.put("token", token); // JWT 토큰 응답
            } else {
                // 회원가입 실패 시 오류 응답
                response.put("statusCode", 400);
                response.put("message", "회원가입 실패");
            }
        } catch (Exception e) {
            // 예외 처리
            response.put("statusCode", 500);
            response.put("message", "회원가입 중 오류가 발생했습니다.");
        }

        return response;
    }
}

