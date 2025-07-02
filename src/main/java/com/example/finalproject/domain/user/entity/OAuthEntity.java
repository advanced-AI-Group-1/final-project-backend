package com.example.finalproject.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OAuth2 인증 정보를 저장하는 엔티티 클래스입니다.
 * 사용자의 소셜 로그인 정보와 해당 공급자(Provider) 정보를 관리합니다.
 *
 * <p>주요 속성:
 * <ul>
 *   <li>id: OAuth 정보의 고유 식별자 (PK)</li>
 *   <li>user: 연관된 사용자 엔티티 (다대일 관계)</li>
 *   <li>providerId: OAuth 제공자에서 부여한 사용자 고유 ID</li>
 *   <li>provider: OAuth 제공자 (google, naver, kakao 등)</li>
 * </ul>
 *
 * <p>데이터베이스 테이블:
 * <ul>
 *   <li>테이블명: OAUTH</li>
 *   <li>인덱스: USER_PK에 대한 외래키 인덱스</li>
 * </ul>
 *
 * <p>사용 예시:
 * <pre>
 * OAuthEntity oauthEntity = new OAuthEntity();
 * oauthEntity.setUser(userEntity);
 * oauthEntity.setProviderId("1234567890");
 * oauthEntity.setProvider("google");
 * </pre>
 *
 * @see UserEntity
 */
@Entity
@Table(name = "OAUTH")
@Getter
@NoArgsConstructor
public class OAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_PK")
    private Long id;

    // ✅ Setter 직접 추가
    @Setter
    @JoinColumn(name = "USER_PK")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    @Setter
    @Column(nullable = false)
    private String providerId;

    @Setter
    @Column(nullable = false)
    private String provider;

}

