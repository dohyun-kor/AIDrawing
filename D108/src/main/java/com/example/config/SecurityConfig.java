package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import com.example.security.JwtAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ✅ Swagger, WebSocket 엔드포인트를 허용할 경로 목록
        String[] swaggerWhitelist = {
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/user/login",   // 로그인 엔드포인트
                "/user/signup",   // 회원가입 엔드포인트
                "/user/nickname/isUsed", // 닉네임 중복 조회
                "/user/isUsed", // 아이디 중복 조회
                "/user/*/info" // 해당 유저 정보 조회
        };

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ws/**") // ✅ WebSocket은 CSRF 보호 제외
                        .disable()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(swaggerWhitelist).permitAll() // ✅ Swagger 관련 경로 허용
                        .requestMatchers("/ws/**").permitAll() // ✅ WebSocket 엔드포인트 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                // ✅ JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults()) // 테스트용 formLogin 활성화
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");  // 모든 도메인 허용
        configuration.addAllowedMethod("*");         // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*");         // 모든 헤더 허용
        configuration.setAllowCredentials(true);     // 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
