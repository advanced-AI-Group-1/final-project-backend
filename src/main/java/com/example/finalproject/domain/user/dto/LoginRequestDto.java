package com.example.finalproject.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String userId;
    private String password;
}




//// 파일 위치: com.example.finalproject.domain.user.dto.LoginRequestDto.java
//
//package com.example.finalproject.domain.user.dto;
//
//public class LoginRequestDto {
//    private String userId;
//    private String password;
//
//    public String getUserId() {
//        return userId;
//    }
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}
