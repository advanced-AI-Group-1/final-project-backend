package com.example.finalproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // Spring Security 설정을 활성화합니다.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정을 Security Filter Chain에 통합
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // API 서버는 세션을 사용하지 않으므로 CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())

                // 요청에 대한 인가(Authorization) 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // '/api/'로 시작하는 모든 요청은 인증 없이 허용
                        .anyRequest().authenticated()           // 그 외의 모든 요청은 인증 필요
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 서버 주소(http://localhost:3000)의 요청을 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        // 허용할 HTTP 메소드 (GET, POST, 등)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 요청 헤더 (모든 헤더 허용)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 쿠키 등 자격 증명을 함께 보낼 수 있도록 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위 CORS 설정을 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}