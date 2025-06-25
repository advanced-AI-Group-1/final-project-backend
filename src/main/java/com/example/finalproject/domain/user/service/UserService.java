package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Builder
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

//    public UserEntity registerUser(String userId, String rawPassword, boolean isDirectSignup) {
//        UserEntity userEntity = UserEntity.builder()
//                .userId(userId)
//                .password(passwordEncoder.encode(rawPassword))
//                .enabled(true)
//                .dateCreated(java.time.LocalDateTime.now())
//                .withdraw(false)
//                .isDirectSignup(isDirectSignup)
//                .build();
//        return userRepository.save(userEntity);
//    }


}
