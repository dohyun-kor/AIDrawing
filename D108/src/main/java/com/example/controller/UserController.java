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
import com.example.model.dto.*;
import com.example.model.service.UserService;
import com.example.util.JWTUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "유저 관련 API")
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
    public ResponseEntity<Boolean> signup(@RequestBody SignUpDto signUpDto) {
        int result = 0;
        try {
            // user 객체에는 nickname, id, password만 포함되어 있다고 가정합니다.
            result = userService.join(signUpDto);
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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        int result = -1;
        try {
            // user 객체에는 id와 password만 포함되어 있다고 가정합니다.
            result = userService.authenticate(loginRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result != -1) {
            // user.getId()를 토큰 subject로 사용하여 토큰 생성(이제는 jwtUtil의 인스턴스 메서드)
            String accessToken = jwtUtil.generateAccessToken(loginRequestDto.getId());
//            String refreshToken = jwtUtil.generateRefreshToken(loginRequestDto.getId());
//            LoginResponseDto response = new LoginResponseDto(result, accessToken, refreshToken);
            LoginResponseDto response = new LoginResponseDto(result, accessToken);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ID 중복 조회
    @GetMapping("/isUsed")
    public ResponseEntity<Boolean> isUsedId(@RequestParam String id) {
        try {
            Boolean isUsed = userService.isUsedId(id);
            return ResponseEntity.ok(isUsed);
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그를 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);  // 오류가 발생했을 경우 false를 반환
        }
    }

    // 닉네임 중복 조회
    @GetMapping("/nickname/isUsed")
    public ResponseEntity<Boolean> isUsedNickname(@RequestParam String nickname) {
        try {
            Boolean isUsed = userService.isUsedNickname(nickname);
            return ResponseEntity.ok(isUsed);
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그를 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);  // 오류가 발생했을 경우 false를 반환
        }
    }
//
//    // 유저 ID로 조회하면 userId를 반환
//    @GetMapping("/{id}")
//    public ResponseEntity<Integer> getUserId(@PathVariable String id) {
//        try {
//            int userId = userService.getUserIdById(id);
//            if (userId != -1) { // 유효한 userId가 반환되었을 경우
//                return ResponseEntity.ok(userId);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 유저를 찾을 수 없을 경우
//            }
//        } catch (Exception e) {
//            e.printStackTrace();  // 예외 로그를 출력
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 오류 발생 시 500 오류 반환
//        }
//    }

    /**
     * userId로 유저 정보를 조회한다.
     * @param userId 조회할 userId
     * @return 유저 정보
     */
    @GetMapping("/{userId}/info")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable int userId) {
        try {
            // userId로 유저 정보를 조회
            UserDto userDto = userService.findByUserId(userId);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그를 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 오류 발생 시 500 오류 반환
        }
    }


    @PatchMapping("/{userId}/profile")
    public ResponseEntity<Boolean> changeProfile(@PathVariable int userId, @RequestBody ChangeProfileDto itemId){
        int result = -1;
        try{
            result = userService.changeProfile(userId, itemId.getItemId());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result > 0){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * nickname으로 유저 정보를 조회한다.
     * @param nickname 조회할 nickname
     * @return 유저 정보
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<UserDto> getUserInfoByNickname(@PathVariable String nickname) {
        try {
            UserDto userDto = userService.findByNickname(nickname);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그를 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 오류 발생 시 500 오류 반환
        }
    }

    // 회원 정보를 수정한다.
    @PatchMapping("{userId}")
    public ResponseEntity<Boolean> updateUserInfo(@PathVariable int userId, @RequestBody SignUpDto signUpDto) {
        int result = 0;
        try {
            result = userService.updateUser(userId, signUpDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == 1) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
