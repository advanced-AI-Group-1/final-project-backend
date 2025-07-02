package com.example.finalproject.domain.user.repository;

import com.example.finalproject.domain.user.entity.EmailVerificationTokenEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends
    JpaRepository<EmailVerificationTokenEntity, Long> {

    EmailVerificationTokenEntity findByToken(String token);

    EmailVerificationTokenEntity findByUser(UserEntity user);
}
