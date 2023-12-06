package com.konggogi.veganlife.member.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
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

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private VegetarianType vegetarianType;

    private int height;
    private int weight;
    private int age;
    private int dailyCarbs;
    private int dailyProtein;
    private int dailyFat;

    @Builder
    public Member(
            String email,
            String nickname,
            String phoneNumber,
            String profileImageUrl,
            Gender gender,
            VegetarianType vegetarianType) {
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.vegetarianType = vegetarianType;
    }
}
