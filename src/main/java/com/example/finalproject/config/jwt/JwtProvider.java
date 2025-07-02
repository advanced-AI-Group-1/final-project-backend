package com.example.finalproject.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

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
@Slf4j
@Component
public class JwtProvider {

    // ✅ 안전한 키를 공식 API로 자동 생성
    private static final SecretKey SECRET_KEY;
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1일

    static {
        try {
            // 안전한 512비트 키 생성
            SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            log.info("JWT 시크릿 키 자동 생성 완료. Base64 인코딩된 키: {}",
                    java.util.Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded()));
        } catch (Exception e) {
            log.error("JWT 시크릿 키 생성 실패", e);
            throw new IllegalStateException("JWT 시크릿 키를 생성할 수 없습니다.", e);
        }
    }


    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY) // ✅ Keys.secretKeyFor() 로 만든 SecretKey는 알고리즘 지정 불필요
                .compact();
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     * @param token 검증할 JWT 토큰
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            log.info("토큰 검증 시도: {}", token);
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

            log.info("토큰 검증 성공 - 사용자: {}, 만료일시: {}",
                claims.getSubject(), claims.getExpiration());
            return true;

        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 서명: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT 서명 검증 실패: {}", e.getMessage());
            log.error("예상 서명 키: {}", java.util.Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded()));
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 오류 발생: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * JWT 토큰에서 인증 정보를 추출합니다.
     * @param token JWT 토큰
     * @return 인증 객체
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 사용자 정보 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 여기서는 단순히 사용자 ID만 추출하지만,
        // 필요에 따라 추가 클레임을 추출하여 권한 정보를 설정할 수 있습니다.
        String userId = claims.getSubject();
        
        // UserDetails 객체 생성 (여기서는 간단히 User 객체를 사용)
        // 실제로는 사용자 서비스를 통해 DB에서 사용자 정보를 조회하는 것이 좋습니다.
        UserDetails principal = new User(userId, "", new ArrayList<>());
        
        // UsernamePasswordAuthenticationToken을 생성하여 반환
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }
}
