package com.example.finalproject.config;

import com.example.finalproject.domain.user.handler.OAuth2SuccessHandler;
import com.example.finalproject.domain.user.service.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 애플리케이션의 보안 설정을 담당하는 클래스입니다.
 * 보안 필터, 인증, 인가 규칙을 구성합니다.
 * OAuth2 로그인 설정과 CSRF 보호 설정을 처리합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>CSRF 보호 비활성화</li>
 *   <li>인증이 필요 없는 경로 설정 (/, /login, /oauth2/** 등)</li>
 *   <li>OAuth2 로그인 설정 및 성공 핸들러 등록</li>
 *   <li>나머지 모든 요청에 대한 인증 요구</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/oauth2/**",
                                "/api/user/signup",
                                "/api/user/login"
                        ).permitAll() // ✅ 여기서 닫고 permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }
}
