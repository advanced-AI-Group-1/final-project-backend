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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login",
                    "/oauth2/**",
                    "/api/user/signup",
                    "/api/user/login",
                    "/api/user/reset-password",
                    "/api/user/send-verification-email",
                    "/api/user/verify-email-code",
                    "/api/user/request-reset-password",
                    "/auth/verify"
                ).permitAll() // ✅ 여기서 닫고 permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            );

        return http.build();

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http
//				.csrf(csrf -> csrf.disable())
//				.authorizeHttpRequests(auth -> auth
//						.requestMatchers("/", "/login", "/oauth2/**").permitAll()
//						.anyRequest().authenticated()
//				)
//				.oauth2Login(oauth2 -> oauth2
//						.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
//						.successHandler(oAuth2SuccessHandler)
//				);
//
//		return http.build();
    }
}
