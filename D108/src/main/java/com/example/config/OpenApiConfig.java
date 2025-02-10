// src/main/java/com/example/config/OpenApiConfig.java
package com.example.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "Authorization";
    @Bean
    public OpenAPI customOpenAPI() {
        final Info info = new Info()
                .title("피리부는 사나이들 API")
                .description("피리부는 사나이들 API Swagger Hompage입니다.");

        return new OpenAPI()
                // API 기본 정보 설정
                .info(info)
                // 서버 정보 추가
                .servers(List.of(
                        new Server().url("https://i12d108.p.ssafy.io/api").description("EC2 Server"),
                        new Server().url("http://localhost:9987/api").description("Local Server")
                                .description("Production server (HTTPS)")))
                // 보안 스키마 설정 추가
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)    // HTTP 방식
                                .scheme("bearer")                    // bearer 방식 사용
                                .bearerFormat("JWT")))               // JWT 형식 명시
                // 모든 API 요청에 대해 위의 보안 스키마 적용
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
