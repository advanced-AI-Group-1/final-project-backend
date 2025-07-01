package com.example.finalproject.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT(JSON Web Token) 생성 및 검증을 담당하는 클래스입니다.
 * 사용자 식별 정보와 만료 시간이 포함된 JWT 토큰을 생성합니다.
 * 
 * <p>주요 기능:
 * <ul>
 *   <li>HS512 서명 알고리즘을 사용한 안전한 JWT 토큰 생성</li>
 *   <li>애플리케이션 시작 시 안전한 비밀 키 자동 생성</li>
 *   <li>기본적으로 1일 후 만료되는 토큰 발급</li>
 * </ul>
 * 
 * <p>참고: 비밀 키는 jjwt 라이브러리의 Keys 유틸리티 클래스를 사용하여
 * 안전하게 자동 생성됩니다.
 */
@Component
public class JwtProvider {

    // ✅ 안전한 키를 공식 API로 자동 생성
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1일

    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY) // ✅ Keys.secretKeyFor() 로 만든 SecretKey는 알고리즘 지정 불필요
                .compact();
    }
}
