
// 파일 위치: src/main/java/com/example/util/JWTUtil.java
package com.example.util;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
/**
 * JWT token 생성 및 검증 유틸리티 클래스
 */
@Component
public class JWTUtil {
    // 주입받을 SecretKey
    private final SecretKey jwtSecretKey;
    // 주입받을 RefreshKey
    private final SecretKey jwtRefreshKey;
    // token 유효기간 (예시: 1일 = 86,400,000ms)
    private static final long ACCESS_EXPIRATION_TIME = 86400000L;
    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    /**
     * 생성자를 통해 SecretKey를 주입받는다.
     * @param jwtSecretKey JWTConfig에서 생성된 access token용 SecretKey Bean
     * @param jwtRefreshKey JWTConfig에서 생성된 refresh token용 SecretKey Bean
     */
    @Autowired
    public JWTUtil(@Qualifier("jwtSecretKey") SecretKey jwtSecretKey,
                   @Qualifier("jwtRefreshKey") SecretKey jwtRefreshKey) {
        this.jwtSecretKey = jwtSecretKey;
        this.jwtRefreshKey = jwtRefreshKey;
    }
    /**
     * access token 생성 메서드
     * @param subject token에 포함할 주체 정보 (예: 사용자 아이디)
     * @return 생성된 JWT token 문자열
     */
    public String generateAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)                                 // token 주체 설정
                .setIssuedAt(new Date())                            // token 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME)) // 만료 시간
                .signWith(jwtSecretKey)                                // 주입받은 비밀키로 서명
                .compact();
    }
    /**
     * refresh token 생성 매서드
     * @param subject token에 포함할 주체 정보 (예: 사용자 아이디)
     * @return 생성된 return token 문자열
     */
    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject) // token 주체 설정
                .setIssuedAt(new Date()) // token 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(jwtRefreshKey) // refresh token용 키로 설정
                .compact();
    }
    /**
     * access token 검증 메서드
     *  - token 파싱 과정에서 유효하지 않은 경우 예외가 발생한다.
     *  - 문제 없이 파싱된다면 유효한 token으로 판단할 수 있습니다.
     *
     * @param token 클라이언트로부터 전달받은 JWT
     * @return token이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateAccessToken(String token) {
        try {
            // parseClaimsJws() 과정에서 서명 검증, 유효기간 검사 등을 수행
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey) // 비밀키 설정
                    .build()
                    .parseClaimsJws(token); // 여기서 예외가 발생하지 않으면 유효한 token
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 시그니처 혹은 구조 문제가 있을 때 발생
            System.out.println("Invalid JWT Signature or Malformed token");
        } catch (ExpiredJwtException e) {
            // token 유효기간 만료
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 형식의 JWT
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            // token이 비어있거나 제대로 구성되지 않았을 때
            System.out.println("JWT claims string is empty or has wrong format");
        }
        return false;
    }
    /**
     * refresh token 검증 메서드
     *  - token 파싱 과정에서 유효하지 않은 경우 예외가 발생한다.
     *  - 문제 없이 파싱된다면 유효한 token으로 판단할 수 있습니다.
     *
     * @param token 클라이언트로부터 전달받은 JWT
     * @return token이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateRefreshToken(String token) {
        try {
            // parseClaimsJws() 과정에서 서명 검증, 유효기간 검사 등을 수행
            Jwts.parserBuilder()
                    .setSigningKey(jwtRefreshKey) // 비밀키 설정
                    .build()
                    .parseClaimsJws(token); // 여기서 예외가 발생하지 않으면 유효한 token
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 시그니처 혹은 구조 문제가 있을 때 발생
            System.out.println("Invalid JWT Signature or Malformed token");
        } catch (ExpiredJwtException e){
            // token 유효기간 만료
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException e){
            // 지원되지 않는 형식의 JWT
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException e){
            // token이 비어있거나 제대로 구성되지 않았을 때
            System.out.println("JWT claims string is empty or has wrong format");
        }
        return false;
    }
    /**
     * access token에서 Subject(사용자 아이디 등)를 추출하는 메서드
     * 검증에 성공한 token이라는 전제 하에 사용할 수 있습니다.
     *
     * @param token 클라이언트로부터 받은 JWT
     * @return token 내부의 subject (사용자 식별자) 반환
     */
    public String getSubjectFromAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    /**
     * refresh token에서 Subject를 추출하는 메서드
     *
     * @param token 클라이언트로부터 받은 JMT
     * @return token 내부의 subject 반환
     */
    public String getSubjectFromRefreshToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtRefreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}

