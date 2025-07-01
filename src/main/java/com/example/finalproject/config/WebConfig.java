package com.example.finalproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS 설정: 프론트엔드에서 접근 허용
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000") // 프론트엔드 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .exposedHeaders("*") // 노출할 헤더
                .allowCredentials(true) // 쿠키 인증 허용
                .maxAge(3600); // preflight 캐시 시간 (1시간)
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
