package com.example.finalproject.domain.user.repository;

import com.example.finalproject.domain.user.entity.OAuthEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<OAuthEntity, Long> {
  Optional<OAuthEntity> findByProviderAndProviderId(String provider, String providerId);
}


