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

import io.jsonwebtoken.*;
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
    /**
     * JWT 토큰 검증 메서드
     *  - 토큰 파싱 과정에서 유효하지 않은 경우 예외가 발생한다.
     *  - 문제 없이 파싱된다면 유효한 토큰으로 판단할 수 있습니다.
     *
     * @param token 클라이언트로부터 전달받은 JWT
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */

    public boolean validateToken(String token) {
        try {
            // parseClaimsJws() 과정에서 서명 검증, 유효기간 검사 등을 수행
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 비밀키 설정
                    .build()
                    .parseClaimsJws(token); // 여기서 예외가 발생하지 않으면 유효한 토큰
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 시그니처 혹은 구조 문제가 있을 때 발생
            System.out.println("Invalid JWT Signature or Malformed token");
        } catch (ExpiredJwtException e){
            // 토큰 유효기간 만료
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException e){
            // 지원되지 않는 형식의 JWT
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException e){
            // 토큰이 비어있거나 제대로 구성되지 않았을 때
            System.out.println("JWT claims string is empty or has wrong format");
        }
        return false;

    }
    /**
     * 토큰에서 Subject(사용자 아이디 등) 을 추출하는 메서드
     * 검증에 성공한 토큰이라는 전제 하에 사용할 수 있습니다.
     *
     * @param token 클라이언트로부터 받은 JWT
     * @return 토큰 내부의 subject (사용자 식별자) 반환
     */
    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
