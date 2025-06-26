package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.UserRepository;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
/**
 * 사용자 관련 핵심 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>userId를 기반으로 사용자 조회</li>
 *   <li>신규 사용자 등록 (회원가입)</li>
 * </ul>
 *
 * <p>의존성:
 * <ul>
 *   <li>{@link UserRepository} - 사용자 데이터베이스 접근을 위한 JPA 리포지토리</li>
 *   <li>{@link PasswordEncoder} - 사용자 비밀번호를 안전하게 암호화하기 위한 컴포넌트</li>
 * </ul>
 *
 * <p>주로 {@code UserController} 또는 인증 관련 컴포넌트에서 호출됩니다.
 *
 * @author (작성자)
 * @since 1.0
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public UserEntity registerUser(String userId, String rawPassword, boolean isDirectSignup) {
        return userRepository.save(     new UserEntity(

                userId,
                passwordEncoder.encode(rawPassword),
                true,
                LocalDateTime.now(),
                null,
                false,
                isDirectSignup

                )
        );
    }

    // 로그인
    public boolean login(String userId, String rawPassword) {
        String password = passwordEncoder.encode(rawPassword);
        return userRepository.findByUserIdAndPassword(userId, password).isPresent();
    }

}