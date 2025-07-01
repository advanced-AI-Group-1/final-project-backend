package com.example.finalproject.domain.user.repository;

import com.example.finalproject.domain.user.entity.OAuthEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

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


