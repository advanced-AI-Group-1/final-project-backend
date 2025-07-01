package com.example.finalproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 인코딩을 위한 설정 클래스입니다.
 * BCrypt 해시 알고리즘을 사용하여 비밀번호를 안전하게 암호화하는 빈을 제공합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>BCryptPasswordEncoder 빈 등록</li>
 *   <li>비밀번호 해시 생성 및 검증</li>
 * </ul>
 */
@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
