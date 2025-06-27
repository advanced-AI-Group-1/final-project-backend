package com.example.finalproject.domain.user.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final String userId; // ✅ 필요한 값만 들고 있음!

    public CustomOAuth2User(OAuth2User oAuth2User, String userId) {
        this.oAuth2User = oAuth2User;
        this.userId = userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }
}
