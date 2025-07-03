package com.example.finalproject.config;

import com.example.finalproject.config.jwt.JwtAuthenticationFilter;
import com.example.finalproject.config.jwt.JwtConfig;
import com.example.finalproject.config.jwt.JwtProvider;
import com.example.finalproject.domain.user.handler.OAuth2SuccessHandler;
import com.example.finalproject.domain.user.service.OAuth2UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.Collections;

/**
 * 애플리케이션의 보안 설정을 담당하는 클래스입니다.
 * CORS, CSRF, 인증, 인가 등의 보안 설정을 관리합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@PostConstruct
	public void setSecurityContextHolderStrategy() {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}

	private final OAuth2UserServiceImpl oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final JwtConfig jwtConfig;
	private final JwtProvider jwtProvider;

	// JwtAuthenticationFilter는 @Bean으로 등록하지 않고 직접 생성하여 사용
	// (순환 참조 문제 방지를 위해)

	private static final String[] PERMIT_ALL_PATTERNS = {
		"/",
		"/api/user/signup",
		"/api/user/login",
		"/api/user/login/**",
		"/api/user/reset-password",
		"/api/user/me",  // 임시로 인증 해제
		"/login/oauth2/code/**",
		"/oauth2/authorization/**",
		"/error",
		"/favicon.ico",
		"/api/user/reset-password",
		"/api/user/send-verification-email",
		"/api/user/verify-email-code",
		"/api/user/request-reset-password",
		"/auth/verify"
	};


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.disable())
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(this.jwtProvider),
				UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write("{\"success\":false,\"message\":\"인증이 필요합니다.\"}");
				})
			)
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
				.successHandler(oAuth2SuccessHandler)
			)
			.logout(logout -> {
				logout.logoutSuccessHandler((request, response, authentication) -> {
					String path = request.getRequestURI();
					if (path.startsWith("/api/")) {
						response.setStatus(HttpServletResponse.SC_OK);
					} else {
						response.sendRedirect("/");
					}
				});
				logout.invalidateHttpSession(true);
				logout.deleteCookies("JSESSIONID");
			});

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
