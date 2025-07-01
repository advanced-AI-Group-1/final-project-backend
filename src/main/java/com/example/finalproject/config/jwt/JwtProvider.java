package com.example.finalproject.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

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
