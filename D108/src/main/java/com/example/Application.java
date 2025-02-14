package com.example;

import com.example.model.dao.UserDao;
import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.example") // 패키지 범위 확인
@MapperScan(basePackageClasses = UserDao.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
