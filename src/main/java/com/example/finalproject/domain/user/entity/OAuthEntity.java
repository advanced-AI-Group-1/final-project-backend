package com.example.finalproject.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "OAUTH")
public class OAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_PK")
    private Long id;

    @JoinColumn(name = "USER_PK")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String provider;

}
