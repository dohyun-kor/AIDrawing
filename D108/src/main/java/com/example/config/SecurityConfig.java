
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
public class SecurityConfig {
    // JwtAuthenticationFilter 주입
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    //    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**","/api/swagger","/api/swagger-ui/**", "/v3/api-docs/**")
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().authenticated()
//                )
//                .build();
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)  // CSRF 전체 비활성화
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // 일단 테스트용으로 전체 허용
//                )
//                .build();
//    }
    //    25-02-09 검증 기본 로직.. swagger에서 Auth 인증 추가 //  회원가입, 로그인은 인증하지 않아도 됨
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // swagger-ui 관련 경로
        String[] swaggerWhitelist = {
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/user/login",   // 로그인 엔드포인트
                "/user/signup" ,   // 회원가입 엔드포인트
                "/user/nickname/isUsed", // 닉네임 중복 조회
                "/user/isUsed", // 아이디 중복 조회
                "/user/*/info" // 해당 유저 정보 조회

        };
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable
                        // Swagger로 들어오는 요청은 CSRF 적용 제외
//                        .ignoringRequestMatchers(swaggerWhitelist)
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 경로는 모두 허용
                        .requestMatchers(swaggerWhitelist).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // formLogin 설정 (테스트를 위해 남겨둠)
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