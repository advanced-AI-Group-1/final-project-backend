package com.example.finalproject.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 나타내는 엔티티 클래스입니다.
 * 데이터베이스의 USERS 테이블과 매핑됩니다.
 *
 * <p>주요 필드:
 * <ul>
 *   <li>id: 사용자 고유 식별자 (PK)</li>
 *   <li>userId: 로그인에 사용되는 사용자 ID (중복 불가)</li>
 *   <li>password: 암호화된 비밀번호</li>
 *   <li>enabled: 계정 활성화 여부</li>
 *   <li>dateCreated: 계정 생성 일시</li>
 *   <li>dateWithdraw: 계정 탈퇴 일시</li>
 *   <li>withdraw: 탈퇴 여부</li>
 *   <li>isDirectSignup: 직접 가입 여부 (소셜 로그인과 구분)</li>
 * </ul>
 */
@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_PK")
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    private LocalDateTime dateWithdraw;

    @Column(nullable = false)
    private boolean withdraw;

    @Column(nullable = false)
    private boolean isDirectSignup;

    public void setPassword(String encode) {
    }

    /**
     * 사용자 탈퇴 처리 메서드
     */
    public void withdraw() {
        this.enabled = false;
        this.withdraw = true;
        this.dateWithdraw = LocalDateTime.now();
    }
}
