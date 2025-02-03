package com.example.controller;

import com.example.model.dto.User;
import com.example.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    private UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping("/user/signup")
    public String signup(@RequestBody User user) {

        // UserService의 join 메서드를 호출하여 회원가입 처리
        int result = userService.join(user);

        // 회원가입 성공 여부 반환
        if (result == 1) {
            return "회원가입 성공";
        } else {
            return "회원가입 실패";
        }
    }


}

