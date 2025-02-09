package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final Info info = new Info()
                .title("피리부는 사나이들 API")
                .description("피리부는 사나이들 API Swagger Hompage입니다.");

        return new OpenAPI()
                .info(info)
                .servers(List.of(
                        new Server().url("https://i12d108.p.ssafy.io/api").description("EC2 Server"),
                        new Server().url("http://localhost:9987/api").description("Local Server")
                        .description("Production server (HTTPS)")));
    }
}
