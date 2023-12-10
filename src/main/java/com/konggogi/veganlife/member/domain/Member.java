package com.konggogi.veganlife.member.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    private String profileImageUrl;

    @Column(nullable = false)
    private String birthYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private VegetarianType vegetarianType;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthType;

    private int height;
    private int weight;
    private int age;
    private int dailyCarbs;
    private int dailyProtein;
    private int dailyFat;

    @Builder
    public Member(String email, String birthYear, String phoneNumber, Role role) {
        this.email = email;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
