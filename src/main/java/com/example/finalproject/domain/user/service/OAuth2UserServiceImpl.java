package com.example.finalproject.domain.user.service;

import com.example.finalproject.domain.user.entity.OAuthEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.OAuthRepository;
import com.example.finalproject.domain.user.repository.UserRepository;
import com.example.finalproject.domain.user.security.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthRepository oAuthRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("✅✅✅ OAuth2UserServiceImpl.loadUser 실행됨");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = null;

        if (provider.equals("google")) {
            providerId = oAuth2User.getAttribute("sub");
        } else if (provider.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
            providerId = (String) response.get("id");
        } else if (provider.equals("kakao")) {
            providerId = String.valueOf(oAuth2User.getAttribute("id"));
        }

        Optional<OAuthEntity> existingOAuth = oAuthRepository.findByProviderAndProviderId(provider, providerId);

        UserEntity user;

        if (existingOAuth.isEmpty()) {
            String newUserId = provider + "_" + providerId;

            Optional<UserEntity> existingUser = userRepository.findByUserId(newUserId);

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = UserEntity.builder()
                        .userId(newUserId)
                        .password(UUID.randomUUID().toString())
                        .enabled(true)
                        .dateCreated(LocalDateTime.now())
                        .withdraw(false)
                        .isDirectSignup(false)
                        .build();
                userRepository.save(user);
            }

            OAuthEntity auth = new OAuthEntity();
            auth.setProvider(provider);
            auth.setProviderId(providerId);
            auth.setUser(user);
            oAuthRepository.save(auth);

        } else {
            user = existingOAuth.get().getUser();
        }

        // ✅ 여기 반드시 user.getUserId() 로 넘겨라!
        return new CustomOAuth2User(oAuth2User, user.getUserId());

    }
}
