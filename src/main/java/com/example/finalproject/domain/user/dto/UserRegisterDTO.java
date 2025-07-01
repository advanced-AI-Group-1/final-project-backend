package com.example.finalproject.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
  @NotBlank(message = "아이디를 입력해주세요.")
  private String userId;
  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;
  @NotNull(message = "isDirectSignup은 true/false로 명시해주세요.")
  private boolean isDirectSignup;
}
