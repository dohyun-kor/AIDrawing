// 파일 위치: src/main/java/com/example/docs/UserControllerDocs.java
package com.example.docs;

import com.example.model.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserControllerDocs {

    @Operation(
            summary = "회원가입 API",
            description =
                    "회원가입 요청 시에는 아래와 같이 **닉네임, 아이디, 패스워드**를 포함해야 합니다.\n\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"nickname\": \"홍길동\",\n" +
                            "  \"id\": \"user123\",\n" +
                            "  \"password\": \"pass1234\"\n" +
                            "}\n" +
                            "```\n"
    )
    ResponseEntity<Boolean> signup(@RequestBody SignUpDto signUpDto);

    @Operation(
            summary = "로그인 API",
            description =
                    "로그인 요청 시에는 아래와 같이 **아이디와 패스워드**만 포함하면 됩니다.\n\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"id\": \"user123\",\n" +
                            "  \"password\": \"pass1234\"\n" +
                            "}\n" +
                            "```\n" +
                            "로그인 성공 시 사용자 ID와 JWT 토큰이 함께 반환됩니다."
    )
    ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto);


    @Operation(
            summary = "아이디의 중복 여부를 조회합니다.",
            description =
                    "입력된 ID가 데이터베이스에 이미 존재하는지 확인하는 API입니다.\n\n" +
                            "### 요청 예시\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"id\": \"user123\",\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> isUsedId(@RequestParam String id);



    @Operation(
            summary = "닉네임의 중복 여부를 조회합니다.",
            description =
                    "입력된 닉네임이 데이터베이스에 이미 존재하는지 확인하는 API입니다.\n\n" +
                            "### 요청 예시\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"nickname\": \"ssafy01\"\n" +
                            "}\n" +
                            "```\n"

    )
    public ResponseEntity<Boolean> isUsedNickname(@RequestParam String nickname);


//    @Operation(
//            summary = "유저 ID로 조회하면 userId를 반환합니다.",
//            description =
//                    "### 요청 예시\n" +
//                            "```json\n" +
//                            "{\n" +
//                            "  \"id\": \"ssafy\"\n" +
//                            "}\n" +
//                            "```\n"
//
//    )
//    public ResponseEntity<Integer> getUserId(@PathVariable String id);

    @Operation(
            summary = "닉네임으로 해당 유저 정보를 조회합니다.",
            description =
                    "주어진 `nickname`에 해당하는 유저의 정보를 조회합니다."
    )
    public ResponseEntity<UserDto> getUserInfoByNickname(@PathVariable String nickname);


    @Operation(
            summary = "해당 유저 정보를 조회합니다.",
            description =
                    "PathVariable에 해당하는 user 정보 조회"
    )
    public ResponseEntity<UserDto> getUserInfo(@PathVariable int userId);


    @Operation(
            summary = "해당 유저의 프로필을 변경합니다.",
            description =
                    "### 요청 예시\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"ItemId\": \"2\"\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> changeProfile(@PathVariable int userId , @RequestBody ChangeProfileDto itemId);

    @Operation(
            summary = "해당 유저의 회원 정보를 수정합니다.",
            description =
                    "주어진 `userId`에 해당하는 유저의 회원 정보를 수정합니다.\n"+
                            "### 요청 예시\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"id\": \"newid\"\n" +
                            "  \"nickname\": \"newnickname\"\n" +
                            "  \"password\": \"newpassword\"\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> updateUserInfo(@PathVariable int userId, @RequestBody SignUpDto signUpDto);

}
