////C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\controller\UserController.java
//package com.example.controller;
//
//import com.example.docs.UserControllerDocs;   // docs 인터페이스 임포트
//import com.example.model.dto.LoginResponse;
//import com.example.model.dto.User;
//import com.example.model.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import com.example.util.JWTUtil;  // JWTUtil 임포트
//
//// 최신 Spring Boot 버전을 기준으로 합니다.
//@RestController
//@RequestMapping("/user")
//@CrossOrigin("*")
//public class UserController implements UserControllerDocs {   // docs 인터페이스 구현
//
//    private final UserService userService;
//    private final JWTUtil jwtUtil; // 주입받을 JWTUtil
//    // 생성자 주입 사용
//    @Autowired
//    public UserController(UserService userService, JWTUtil jwtUtil) {
//        this.userService = userService;
//
//        this.jwtUtil = jwtUtil;
//    }
//
//    /**
//     * 회원가입 API
//     * 회원가입 시에는 닉네임, 아이디, 패스워드만 전달됩니다.
//     */
//    @PostMapping("/signup")
//    public ResponseEntity<Boolean> signup(@RequestBody User user) {
//        int result = 0;
//        try {
//            // user 객체에는 nickname, id, password만 포함되어 있다고 가정합니다.
//            result = userService.join(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if(result == 1) {
//            return ResponseEntity.ok(true);
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * 로그인 API
//     * 로그인 시에는 아이디와 패스워드만 전달됩니다.
//     */
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
//        int result = -1;
//        try {
//            // user 객체에는 id와 password만 포함되어 있다고 가정합니다.
//            result = userService.authenticate(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if(result != -1) {
//            // user.getId()를 토큰 subject로 사용하여 토큰 생성
//            String token = JWTUtil.generateToken(user.getId());
//            LoginResponse response = new LoginResponse(result,token);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }
//}

package com.example.controller;

import com.example.docs.UserControllerDocs;
import com.example.model.dto.LoginResponse;
import com.example.model.dto.User;
import com.example.model.service.UserService;
import com.example.util.JWTUtil;  // 변경된 JWTUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final JWTUtil jwtUtil; // 주입받을 JWTUtil

    // 생성자 주입 사용
    @Autowired
    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 회원가입 API
     * 회원가입 시에는 닉네임, 아이디, 패스워드만 전달됩니다.
     */
    @PostMapping("/signup")
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
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        int result = -1;
        try {
            // user 객체에는 id와 password만 포함되어 있다고 가정합니다.
            result = userService.authenticate(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result != -1) {
            // user.getId()를 토큰 subject로 사용하여 토큰 생성(이제는 jwtUtil의 인스턴스 메서드)
            String token = jwtUtil.generateToken(user.getId());
            LoginResponse response = new LoginResponse(result, token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ID 중복 조회
    @GetMapping("/isUsed")
    public Boolean isUsedId(@RequestParam String id) {
        return userService.isUsedId(id);
    }

    // 닉네임 중복 조회
    @GetMapping("/nickname/isUsed")
    public Boolean isUsedNickname(@RequestParam String nickname) {
        return userService.isUsedNickname(nickname);
    }

}
