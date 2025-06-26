package com.example.finalproject.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public UserEntity(String userId, String password, boolean enabled, LocalDateTime dateCreated, LocalDateTime dateWithdraw, boolean withdraw, boolean isDirectSignup) {
        this.userId = userId;
        this.password = password;
        this.enabled = enabled;
        this.dateCreated = dateCreated;
        this.dateWithdraw = dateWithdraw;
        this.withdraw = withdraw;
        this.isDirectSignup = isDirectSignup;
    }
}
