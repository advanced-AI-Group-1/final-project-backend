package com.example.finalproject.domain.user.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class OAuthLoginDTO {
    private Long id;
    private String userId;
    private String providerId;
    private String provider;
}
