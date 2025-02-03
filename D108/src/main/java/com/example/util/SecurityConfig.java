package com.example.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

@SuppressWarnings("deprecation")
@Configuration // 스프링 환경 세팅을 돕는 어노테이션
@EnableWebSecurity // 스프링 시큐리티 설정할 클래스라고 알려주는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter {


}
