package com.example.finalproject.domain.user.controller;

import com.example.finalproject.domain.user.dto.UserResponseDTO;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponseDTO getUserInfo(@PathVariable String userId) {
        UserEntity userEntity = userService.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponseDTO.of(userEntity);
    }

    @PostMapping("/register")
    public String register(@RequestParam String userId,
                           @RequestParam String rawPassword,
                           @RequestParam boolean isDirectSignup) {
        userService.registerUser(userId, rawPassword, isDirectSignup);
        return "User registered successfully";
    }
}
