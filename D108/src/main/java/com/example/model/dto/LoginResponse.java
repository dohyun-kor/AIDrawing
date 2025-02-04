// 파일 위치: src/main/java/com/example/model/dto/LoginResponse.java
package com.example.model.dto;

/**
 * 로그인 응답을 위한 DTO 클래스
 * 사용자 아이디와 JWT 토큰을 함께 반환합니다.
 */
public class LoginResponse {

    // 사용자 식별자
    private int userId;

    // JWT 토큰 문자열
    private String token;

    // 기본 생성자
    public LoginResponse() {
    }

    // 모든 필드를 초기화하는 생성자
    public LoginResponse(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    // Getter, Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                '}';
    }
}
