
package com.example.model.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 로그인 시 사용하는 DTO
 */
@Getter
@Setter
@ToString
public class LoginRequestDto {
    private String id;
    private String password;
    public LoginRequestDto(String id, String password) {
        this.id = id;
        this.password = password;
    }
}