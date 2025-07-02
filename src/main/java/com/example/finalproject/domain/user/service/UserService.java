package com.example.finalproject.domain.user.service;

import com.example.finalproject.config.jwt.JwtProvider;
import com.example.finalproject.domain.user.entity.OAuthEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.OAuthRepository;
import com.example.finalproject.domain.user.repository.UserRepository;
import com.example.finalproject.exception.error.DuplicateUserException;
import com.example.finalproject.exception.error.UnAuthorizedException;
import com.example.finalproject.exception.error.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final OAuthRepository oauthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Optional<UserEntity> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public UserEntity registerUser(String userId, String rawPassword, boolean isDirectSignup) {
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateUserException("이미 존재하는 사용자입니다: " + userId);
        }

        return userRepository.save(
                UserEntity.builder()
                        .userId(userId)
                        .password(passwordEncoder.encode(rawPassword))
                        .enabled(true)
                        .dateCreated(LocalDateTime.now())
                        .dateWithdraw(null)
                        .withdraw(false)
                        .isDirectSignup(isDirectSignup)
                        .build()
        );
    }

    public String login(String userId, String password) {
        log.info("로그인 시도 - 사용자 ID: {}", userId);
        Optional<UserEntity> user = userRepository.findByUserId(userId);
        
        if (user.isEmpty()) {
            log.warn("사용자를 찾을 수 없음 - ID: {}", userId);
            throw new RuntimeException("아이디 또는 비밀번호를 확인해주세요.");
        }
        
        // 탈퇴한 회원인지 확인
        if (user.get().isWithdraw()) {
            log.warn("탈퇴한 회원 로그인 시도 - ID: {}", userId);
            throw new RuntimeException("탈퇴한 회원입니다.");
        }
        
        boolean passwordMatches = passwordEncoder.matches(password, user.get().getPassword());
        log.info("비밀번호 일치 여부: {}", passwordMatches ? "일치" : "불일치");
        
        if (passwordMatches) {
            String token = jwtProvider.generateToken(userId);
            log.info("로그인 성공 - 토큰 생성됨");
            return token;
        }
        log.warn("로그인 실패 - ID: {}", userId);
        return null;
    }

    /**
     * 사용자 탈퇴 처리 메서드
     * 직접 가입한 사용자의 경우 비밀번호 검증을 수행하고, OAuth 사용자는 비밀번호 검증 없이 탈퇴 처리
     */
    @Transactional
    public void withdrawUser(String userId, String password) {
        // 사용자 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 이미 탈퇴한 사용자인지 확인
        if (user.isWithdraw()) {
            throw new UserNotFoundException("이미 탈퇴한 사용자입니다.");
        }

        // 직접 가입한 사용자인 경우 비밀번호 검증
        if (user.isDirectSignup()) {
            if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
                throw new UnAuthorizedException("비밀번호가 일치하지 않습니다.");
            }
        }

        // OAuth 연동 정보가 있는 경우 삭제
        List<OAuthEntity> oauthEntities = oauthRepository.findByUser(user);
        if (!oauthEntities.isEmpty()) {
            oauthRepository.deleteAll(oauthEntities);
        }

        // 사용자 탈퇴 처리 (소프트 딜리트)
        user.withdraw();
        userRepository.save(user);
    }

    public void updatePassword(String email, String newPassword) {
        UserEntity user = userRepository.findByUserId(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}