package com.example.finalproject.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {

    private String id;        // 이메일 형식의 아이디
    private String password;  // 새로운 비밀번호
}
