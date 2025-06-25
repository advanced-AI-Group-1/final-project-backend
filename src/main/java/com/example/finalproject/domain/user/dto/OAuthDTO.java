package com.example.finalproject.domain.user.dto;

import lombok.Data;

@Data
public class OAuthDTO {
    private Long id;
    private String userId;
    private String providerId;
    private String provider;
}
