// 파일 위치: src/main/java/com/example/model/dto/LoginResponse.java
package com.example.model.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * 로그인 응답을 위한 DTO 클래스
 * 사용자 아이디와 JWT 토큰을 함께 반환합니다.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class LoginResponseDto {
    // 사용자 식별자
    private int userId;
    // JWT access 토큰 문자열
    private String token;
    //    // JWY Refresh 토큰 문자열
//    private String refreshToken;
    // 모든 필드를 초기화하는 생성자
    public LoginResponseDto(int userId, String token) {
        this.userId = userId;
        this.token = token;
//        this.refreshToken = refreshToken;
    }
}
