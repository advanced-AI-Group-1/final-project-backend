package com.example.finalproject.domain.user.controller;

import com.example.finalproject.domain.user.dto.LoginRequestDto;
import com.example.finalproject.domain.user.dto.UserDto;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ 테스트용 GET 요청 추가 (Postman에서 확인용)
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("테스트 성공");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {
        userService.signup(userDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        System.out.println("📩 POST /login 요청 도착: " + request.getUserId()); // (테스트를 위해 추가됨)
        boolean success = userService.login(request.getUserId(), request.getPassword());
        if (success) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }

    // 유저 조회 (테스트용 GET)
    @GetMapping("/{userId}")
    public UserEntity getUser(@PathVariable String userId) {
        return userService.findByUserId(userId);
    }
}
