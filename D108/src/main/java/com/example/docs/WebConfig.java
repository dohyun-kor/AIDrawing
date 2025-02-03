package com.example.docs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 모든 Origin 허용
                .allowedOrigins("*")
                // CORS Preflight 요청에 대해 허용할 메서드 (GET, POST, PUT, DELETE 등)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        // 필요하다면 허용 헤더/크리덴셜 설정 추가
        // .allowedHeaders("Content-Type", "Authorization")
        // .allowCredentials(true)
        ;
    }
}
