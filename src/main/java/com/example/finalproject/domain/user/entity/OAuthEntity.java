//package com.example.finalproject.domain.user.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "OAUTH")
//public class OAuthEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "OAUTH_PK")
//    private Long id;
//
//    @JoinColumn(name = "USER_PK")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private UserEntity user;
//
//    @Column(nullable = false)
//    private String providerId;
//
//    @Column(nullable = false)
//    private String provider;
//
//}


package com.example.finalproject.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OAUTH")
@Getter
@NoArgsConstructor
public class OAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_PK")
    private Long id;

    @JoinColumn(name = "USER_PK")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String provider;

    // ✅ Setter 직접 추가
    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}

