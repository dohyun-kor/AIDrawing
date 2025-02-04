// 파일 위치: src/main/java/com/example/docs/JWTConfig.java
package com.example.docs;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 관련 설정을 위한 Configuration 클래스
 */
@Configuration
public class JWTConfig {

    // application.properties 에서 jwt.secret 값을 주입받음.
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * SecretKey Bean 생성
     * @return HMAC-SHA 알고리즘에 사용할 SecretKey
     */
    @Bean
    public SecretKey jwtSecretKey() {
        // jwtSecret 문자열을 바이트 배열로 변환하여 SecretKey 생성
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
