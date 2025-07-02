package com.example.finalproject.domain.user.handler;

import com.example.finalproject.config.jwt.JwtProvider;
import com.example.finalproject.domain.user.security.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 인증 성공 시 호출되는 핸들러 클래스입니다.
 * 인증 성공 후 JWT 토큰을 생성하고 프론트엔드로 리다이렉트합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>OAuth2 인증 성공 시 호출됨</li>
 *   <li>사용자 ID를 기반으로 JWT 토큰 생성</li>
 *   <li>생성된 토큰을 쿼리 파라미터로 포함하여 프론트엔드로 리다이렉트</li>
 * </ul>
 *
 * <p>동작 흐름:
 * <ol>
 *   <li>OAuth2 인증 성공 시 Spring Security에 의해 자동 호출</li>
 *   <li>인증된 사용자 정보에서 사용자 ID 추출</li>
 *   <li>JWT 토큰 생성</li>
 *   <li>생성된 토큰을 포함한 URL로 리다이렉트 (http://localhost:3000/?token=...)</li>
 * </ol>
 *
 * @see JwtProvider
 * @see CustomOAuth2User
 */
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        String userId = customUser.getUserId();
        String token = jwtProvider.generateToken(userId);

        response.sendRedirect("http://localhost:3000/?token=" + token);
    }
}
