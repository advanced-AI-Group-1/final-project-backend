package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.dto.UserDto;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

    // 로그인
    public boolean login(String userId, String password) {
        return userRepository.findByUserIdAndPassword(userId, password).isPresent();
    }

    // 유저 조회 (GET 테스트용)
    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }
}
