package com.example.finalproject.domain.user.dto;

import com.example.finalproject.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String userId;
    //    private String password; // 보안상 제외
    private boolean enabled;
    private LocalDateTime dateCreated;
    private LocalDateTime dateWithdraw;
    private boolean withdraw;
    private boolean isDirectSignup;

    public static UserResponseDTO of(UserEntity userEntity) {
        return UserResponseDTO.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .enabled(userEntity.isEnabled())
                .dateCreated(userEntity.getDateCreated())
                .dateWithdraw(userEntity.getDateWithdraw())
                .withdraw(userEntity.isWithdraw())
                .isDirectSignup(userEntity.isDirectSignup())
                .build();
    }
}
