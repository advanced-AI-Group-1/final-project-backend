package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.entity.OAuthEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.OAuthRepository;
import com.example.finalproject.domain.user.repository.UserRepository;
import com.example.finalproject.exception.error.DuplicateUserException;
import com.example.finalproject.exception.error.UnAuthorizedException;
import com.example.finalproject.exception.error.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OAuthRepository oauthRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

//    public UserEntity registerUser(String userId, String rawPassword, boolean isDirectSignup) {
//        return userRepository.save(
//                new UserEntity(
//                        userId,
//                        passwordEncoder.encode(rawPassword),
//                        true,
//                        LocalDateTime.now(),
//                        null,
//                        false,
//                        isDirectSignup
//                )
//        );
//    }

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

    public boolean login(String userId, String rawPassword) {
        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }
        return false;
    }

    /**
     * 사용자 탈퇴 처리 메서드 직접 가입한 사용자의 경우 비밀번호 검증을 수행하고, OAuth 사용자는 비밀번호 검증 없이 탈퇴 처리
     *
     * @param userId   탈퇴할 사용자 ID
     * @param password 사용자 비밀번호 (직접 가입한 사용자의 경우 필수)
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws UnAuthorizedException 비밀번호가 일치하지 않는 경우 (직접 가입 사용자)
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

    @Transactional
    public void resetPassword(String userId, String newPassword) {
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
