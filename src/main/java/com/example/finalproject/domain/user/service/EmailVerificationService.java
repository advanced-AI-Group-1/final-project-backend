package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.entity.EmailVerificationTokenEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;

    @Transactional
    public String createEmailVerificationToken(UserEntity user) {
        // 기존 토큰 삭제
        EmailVerificationTokenEntity existingToken = tokenRepository.findByUser(user);
        if (existingToken != null) {
            tokenRepository.delete(existingToken);
            tokenRepository.flush(); // 삭제 즉시 DB에 반영 강제
        }

        String token = UUID.randomUUID().toString();
        EmailVerificationTokenEntity tokenEntity = EmailVerificationTokenEntity.builder()
            .token(token)
            .user(user)
            .expiryDate(LocalDateTime.now().plusHours(24))
            .build();
        tokenRepository.save(tokenEntity);
        log.info("Created email verification token for user: {}", user.getUserId());
        return token;
    }
}

