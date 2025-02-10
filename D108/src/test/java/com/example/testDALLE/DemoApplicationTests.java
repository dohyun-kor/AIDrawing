package com.example.testDALLE;

import com.example.Application; // 메인 애플리케이션 클래스 임포트
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest(classes = Application.class) // 설정 클래스 명시
public class DemoApplicationTests {

	@Test
	void contextLoads() {
	}
}
