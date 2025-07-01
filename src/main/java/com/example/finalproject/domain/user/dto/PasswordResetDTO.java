package com.example.finalproject.domain.user.dto;

public class PasswordResetDTO {

    private String id;        // 이메일 형식의 아이디
    private String password;  // 새로운 비밀번호

    public PasswordResetDTO() {
    }

    public PasswordResetDTO(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
