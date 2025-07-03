package com.example.finalproject.config.jwt;

import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	// 인증이 필요 없는 경로 목록
	private static final String[] PERMIT_ALL_PATTERNS = {
		"/",
		"/api/user/signup",
		"/api/user/login",
		"/api/user/login/**",
		"/api/user/reset-password",
		"/api/user/me",
		"/login/oauth2/code/**",
		"/oauth2/authorization/**",
		"/error",
		"/favicon.ico"
	};

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		log.info("shouldNotFilter 호출: {}", path);
		return Arrays.asList(PERMIT_ALL_PATTERNS).contains(path);
	}

	public JwtAuthenticationFilter(JwtProvider jwtProvider) {
		log.info("JwtAuthenticationFilter 생성자 호출됨");
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		log.info("doFilterInternal 호출: {}", request.getRequestURI());
		try {
			log.info("=== JWT 인증 필터 시작 ===");
			log.info("요청 URL: {} {}", request.getMethod(), request.getRequestURL().toString());

			// 헤더 로깅 (디버그 레벨로 변경)
			if (log.isDebugEnabled()) {
				Enumeration<String> headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					log.debug("헤더 - {}: {}", headerName, request.getHeader(headerName));
				}
			}

			String token = resolveToken(request);

			if (token == null) {
				log.warn("JWT 토큰을 찾을 수 없습니다. Authorization 헤더를 확인해주세요.");
			} else {
				log.info("추출된 토큰: {}... (길이: {})",
					token.substring(0, Math.min(20, token.length())),
					token.length());

				// 토큰 검증 시도
				boolean isValid = jwtProvider.validateToken(token);
				log.info("토큰 검증 결과: {}", isValid ? "유효함" : "유효하지 않음");

				if (isValid) {
					try {
						Authentication authentication = jwtProvider.getAuthentication(token);
						if (authentication != null) {
							log.info("인증 정보 생성 성공 - 사용자: {}, 권한: {}",
								authentication.getName(),
								authentication.getAuthorities());

							SecurityContext context = SecurityContextHolder.createEmptyContext();
							context.setAuthentication(authentication);
							SecurityContextHolder.setContext(context);

							log.info("SecurityContext에 인증 정보 설정 완료");
						} else {
							log.warn("인증 정보를 생성할 수 없습니다.");
						}
					} catch (Exception e) {
						log.error("인증 정보 생성 중 오류 발생: {}", e.getMessage(), e);
					}
				}
			}

			filterChain.doFilter(request, response);
			log.info("=== JWT 인증 필터 종료 ===");
		} catch (Exception e) {
			log.error("JWT 인증 필터에서 오류 발생: {}", e.getMessage(), e);
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "인증에 실패했습니다: " + e.getMessage());
		}
	}

	// Request Header에서 토큰 정보 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
