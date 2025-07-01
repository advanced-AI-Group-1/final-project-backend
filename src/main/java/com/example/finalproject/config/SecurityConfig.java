//package com.example.finalproject.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http
//			.csrf(csrf -> csrf.disable())
//			.authorizeHttpRequests(auth -> auth
//				.anyRequest().permitAll()
//			);
//		return http.build();
//	}
//}

package com.example.finalproject.config;

import com.example.finalproject.domain.user.handler.OAuth2SuccessHandler;
import com.example.finalproject.domain.user.service.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2UserServiceImpl oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configure(http)) // CORS 설정 활성화
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/login", "/oauth2/**", 
						"/api/query/**", "/api/user/test", 
						"/api/report/**", "/api/report/download-json/**",
						"/h2-console/**").permitAll() // H2 콘솔 접근 허용
						.anyRequest().authenticated()
				)
				.headers(headers -> headers.frameOptions().disable()) // H2 콘솔을 위한 frame 옵션 비활성화
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
						.successHandler(oAuth2SuccessHandler)
				);

		return http.build();
	}
}
