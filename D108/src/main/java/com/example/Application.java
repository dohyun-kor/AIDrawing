package com.example;

import com.example.model.dao.UserDao;
import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "com.example") // 패키지 범위 확인
@MapperScan(basePackageClasses = UserDao.class)
public class Application {
    public static void main(String[] args) {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.load();
        // .env 파일에서 값을 읽어 시스템 속성에 설정 (환경 변수로 사용)
        System.setProperty("openai.api.key", dotenv.get("OPENAI_API_KEY"));

        SpringApplication.run(Application.class, args);
    }
}
