package com.example.finalproject.domain.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER")
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
    
}
