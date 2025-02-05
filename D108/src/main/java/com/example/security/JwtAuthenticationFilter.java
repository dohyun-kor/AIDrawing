package com.example.security;

import com.example.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 토큰을 검증하는 Spring Security 필터 클래스
 * - 모든 HTTP 요청마다 실행되어, 토큰이 유효한 경우 SecurityContextHolder에 인증 객체를 저장합니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWTUtil을 주입받아서 사용할 수 있도록 필드 선언
    private final JWTUtil jwtUtil;

    // 생성자 주입
    public JwtAuthenticationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 요청이 필터를 통과할 때마다 수행되는 메서드
     * @param request  HTTP 요청 정보
     * @param response HTTP 응답 정보
     * @param filterChain 필터 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) Authorization 헤더로부터 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        // 2) 헤더 값이 비어있지 않고 "Bearer "로 시작하는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // "Bearer " 제거 후 실제 토큰 문자열만 추출
            String token = authorizationHeader.substring(7);

            try {
                // 3) 토큰 검증
                if (jwtUtil.validateToken(token)) {
                    // 유효한 토큰인 경우, 토큰에서 사용자 식별자(subject) 추출
                    String userId = jwtUtil.getSubject(token);

                    // 4) 임시로 UsernamePasswordAuthenticationToken을 생성
                    //    - 실제로는 userId로 DB 조회해 UserDetails 를 만들 수도 있음
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userId,       // Principal (사용자 식별값)
                                    null,         // Credentials (일단 null)
                                    null          // Authorities (권한 정보가 있다면 넣어줄 수 있음)
                            );

                    // 5) SecurityContextHolder 에 인증 객체 저장 → 이후 @AuthenticationPrincipal 등에서 사용 가능
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (ExpiredJwtException e) {
                // 만료된 토큰인 경우
                System.out.println("[JwtAuthenticationFilter] 만료된 토큰입니다: " + e.getMessage());
            } catch (Exception e) {
                // 그 외 오류 처리
                System.out.println("[JwtAuthenticationFilter] 유효하지 않은 토큰입니다: " + e.getMessage());
            }
        }

        // 6) 체인 상의 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
