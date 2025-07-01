package com.example.finalproject.domain.user.repository;

import com.example.finalproject.domain.user.entity.OAuthEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OAuth2 인증 정보를 관리하는 리포지토리 인터페이스입니다.
 * 사용자의 소셜 로그인 정보를 데이터베이스에서 조회하고 관리합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>공급자(Provider)와 공급자 ID로 OAuth 정보 조회</li>
 *   <li>사용자별로 연결된 모든 OAuth 정보 조회</li>
 *   <li>기본 CRUD 기능 (JpaRepository 상속)</li>
 * </ul>
 *
 * <p>사용 예시:
 * <pre>
 * // 특정 공급자의 사용자 정보 조회
 * Optional<OAuthEntity> oauthInfo = oauthRepository.findByProviderAndProviderId("google", "1234567890");
 *
 * // 사용자의 모든 OAuth 연결 정보 조회
 * List<OAuthEntity> userOAuths = oauthRepository.findByUser(userEntity);
 * </pre>
 *
 * @see com.example.finalproject.domain.user.entity.OAuthEntity
 * @see com.example.finalproject.domain.user.entity.UserEntity
 */
public interface OAuthRepository extends JpaRepository<OAuthEntity, Long> {
    Optional<OAuthEntity> findByProviderAndProviderId(String provider, String providerId);
    
    /**
     * 사용자에 해당하는 모든 OAuth 연동 정보를 조회합니다.
     * 
     * @param user 조회할 사용자 엔티티
     * @return 해당 사용자의 OAuth 연동 정보 리스트
     */
    List<OAuthEntity> findByUser(UserEntity user);
}


