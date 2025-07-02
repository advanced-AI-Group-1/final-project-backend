package com.example.finalproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 애플리케이션의 전반적인 웹 구성을 담당하는 클래스입니다.
 * CORS(Cross-Origin Resource Sharing) 설정을 통해 프론트엔드 애플리케이션의 요청을 허용합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>CORS 설정을 통한 프론트엔드(로컬호스트:3000) 접근 허용</li>
 *   <li>HTTP 메서드(GET, POST, PUT, DELETE, OPTIONS) 허용</li>
 *   <li>모든 헤더 허용</li>
 *   <li>쿠키 인증 허용 (세션 사용 시 필요)</li>
 * </ul>
 */
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
