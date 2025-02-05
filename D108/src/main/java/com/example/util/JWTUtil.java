//// 파일 위치: src/main/java/com/example/util/JWTUtil.java
//package com.example.util;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.security.Key;
//import java.util.Date;
//
///**
// * JWT 토큰 생성 및 검증 유틸리티 클래스
// */
//public class JWTUtil {
//
//    // 비밀키 (실제 운영환경에서는 안전하게 관리해야 하며, 환경변수나 별도 설정 파일에 저장)
//    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//
//    // 토큰 유효기간 (예시: 1일)
//    private static final long EXPIRATION_TIME = 86400000L; // 24시간 (밀리초 단위)
//
//    /**
//     * JWT 토큰 생성 메서드
//     * @param subject 토큰에 포함할 주체 정보 (예: 사용자 아이디)
//     * @return 생성된 JWT 토큰 문자열
//     */
//    public static String generateToken(String subject) {
//        return Jwts.builder()
//                .setSubject(subject)                         // 토큰 주체 설정
//                .setIssuedAt(new Date())                       // 토큰 발행 시간
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
//                .signWith(key)                                 // 비밀키로 서명
//                .compact();
//    }
//
//    // 토큰 검증, 파싱 등의 추가 메서드를 필요에 따라 구현할 수 있음
//}
//

package com.example.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티 클래스
 */
@Component
public class JWTUtil {

    // 주입받을 SecretKey
    private final SecretKey secretKey;

    // 토큰 유효기간 (예시: 1일 = 86,400,000ms)
    private static final long EXPIRATION_TIME = 86400000L;

    /**
     * 생성자를 통해 SecretKey를 주입받는다.
     * @param secretKey JWTConfig에서 생성된 SecretKey Bean
     */
    public JWTUtil(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * JWT 토큰 생성 메서드
     * @param subject 토큰에 포함할 주체 정보 (예: 사용자 아이디)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)                                 // 토큰 주체 설정
                .setIssuedAt(new Date())                            // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(secretKey)                                // 주입받은 비밀키로 서명
                .compact();
    }

    // 필요에 따라 토큰 검증, 파싱 등 추가 메서드를 구현할 수 있음
}
