package com.example.finalproject.domain.user.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String token;      // 이메일 인증 토큰
    private String password;   // 새 비밀번호
}