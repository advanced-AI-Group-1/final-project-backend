package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.dto.UserDto;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
@Builder
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    // 회원가입
    public void signup(UserDto userDto) {
        UserEntity user = UserEntity.builder()
                .userId(userDto.getUserId())
                .password(userDto.getPassword())  // 평문 저장 (현재 요구사항 기준)
                .enabled(true)
                .dateCreated(LocalDateTime.now())
                .withdraw(false)
                .isDirectSignup(true)
                .build();
        userRepository.save(user);
    }

    public UserEntity registerUser(String userId, String rawPassword, boolean isDirectSignup) {
        return userRepository.save(
            new UserEntity(
                userId,
                passwordEncoder.encode(rawPassword),
                true,
                LocalDateTime.now(),
                null,
                false,
                isDirectSignup
            )
        );
    // 로그인
    public boolean login(String userId, String password) {
        return userRepository.findByUserIdAndPassword(userId, password).isPresent();
    }

    // 유저 조회 (GET 테스트용)
    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }
}
