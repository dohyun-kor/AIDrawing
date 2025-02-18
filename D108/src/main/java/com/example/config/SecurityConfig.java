package com.example.config;

import com.example.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
        // ✅ Swagger, WebSocket, Item 엔드포인트를 허용할 경로 목록
        String[] whitelistPaths = {
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/user/login",
                "/user/signup",
                "/user/nickname/isUsed",
                "/user/isUsed",
                "/user/*/info",
                "/static/item/**",  // Item 이미지에 대한 경로 추가
                "/item/**",  // Item 이미지에 대한 경로 추가
                "/myItems/**",
                "/picture/upload/**"
                "/myroom/**"
        };

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ws/**")
                        .disable()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelistPaths).permitAll() // 화이트리스트 경로 허용
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
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
