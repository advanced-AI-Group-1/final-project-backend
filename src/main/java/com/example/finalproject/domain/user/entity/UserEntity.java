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
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
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

    public UserEntity(String userId, String password, boolean enabled, LocalDateTime dateCreated,
        LocalDateTime dateWithdraw, boolean withdraw, boolean isDirectSignup) {
        this.userId = userId;
        this.password = password;
        this.enabled = enabled;
        this.dateCreated = dateCreated;
        this.dateWithdraw = dateWithdraw;
        this.withdraw = withdraw;
        this.isDirectSignup = isDirectSignup;
    }

    /**
     * 사용자 탈퇴 처리 메서드 - 계정 비활성화 - 탈퇴 여부 표시 - 탈퇴 일시 기록
     */
    public void withdraw() {
        this.enabled = false;
        this.withdraw = true;
        this.dateWithdraw = LocalDateTime.now();
    }
}
