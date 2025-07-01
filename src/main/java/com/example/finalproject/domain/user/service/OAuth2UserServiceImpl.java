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

/**
 * OAuth2 인증 처리를 위한 서비스 구현체입니다.
 * Google, Naver, Kakao 등 다양한 OAuth2 제공자로부터 사용자 정보를 로드하고 처리합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>OAuth2 제공자(Google, Naver, Kakao)로부터 사용자 정보 로드</li>
 *   <li>신규 사용자 자동 등록</li>
 *   <li>기존 사용자 정보 업데이트</li>
 *   <li>OAuth2 인증 후 사용자 정보를 CustomOAuth2User로 변환</li>
 * </ul>
 *
 * <p>동작 방식:
 * <ul>
 *   <li>OAuth2 제공자로부터 사용자 정보를 로드합니다.</li>
 *   <li>provider와 providerId를 기반으로 기존 사용자 여부를 확인합니다.</li>
 *   <li>기존 사용자가 없는 경우 자동으로 회원가입을 처리합니다.</li>
 *   <li>인증된 사용자 정보를 CustomOAuth2User 객체로 변환하여 반환합니다.</li>
 * </ul>
 *
 * <p>주의사항:
 * <ul>
 *   <li>각 OAuth 제공자별로 사용자 정보 추출 방식이 다릅니다.</li>
 *   <li>사용자 이메일이 없는 경우 랜덤 UUID를 사용하여 임시 이메일을 생성합니다.</li>
 * </ul>
 */
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

        switch (provider) {
            case "google" -> providerId = oAuth2User.getAttribute("sub");
            case "naver" -> {
                Map<String, Object> response = oAuth2User.getAttribute("response"); // 오류시 수정할 것 ->Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                assert response != null;
                providerId = (String) response.get("id");
            }
            case "kakao" -> providerId = String.valueOf(oAuth2User.getAttribute("id"));
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
