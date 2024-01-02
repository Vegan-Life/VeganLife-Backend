package com.konggogi.veganlife.member.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import jakarta.persistence.*;
import java.time.LocalDate;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Column(nullable = false)
    private String oauthUserId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String profileImageUrl;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VegetarianType vegetarianType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Integer weight;

    private Integer dailyCarbs;
    private Integer dailyProtein;
    private Integer dailyFat;
    private Integer AMR; // 활동 대사량

    @Builder
    public Member(
            Long id,
            OauthProvider oauthProvider,
            String oauthUserId,
            String email,
            String nickname,
            String profileImageUrl,
            Integer birthYear,
            Gender gender,
            VegetarianType vegetarianType,
            Integer height,
            Integer weight,
            Integer dailyCarbs,
            Integer dailyProtein,
            Integer dailyFat,
            Integer AMR) {
        this.id = id;
        this.oauthProvider = oauthProvider;
        this.oauthUserId = oauthUserId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.vegetarianType = vegetarianType;
        this.birthYear = birthYear;
        this.height = height;
        this.weight = weight;
        this.dailyCarbs = dailyCarbs;
        this.dailyProtein = dailyProtein;
        this.dailyFat = dailyFat;
        this.AMR = AMR;
        this.role = Role.USER;
    }

    public void updateDailyIntake() {
        int bmr = calcBMR();
        AMR = (int) Math.round(bmr * 1.375);
        dailyCarbs = (int) ((AMR * (50.0 / 100)) / 4);
        dailyProtein = (int) ((AMR * (30.0 / 100)) / 4);
        dailyFat = (int) ((AMR * (20.0 / 100)) / 9);
    }

    public void modifyMemberProfile(
            String nickname,
            String profileImageUrl,
            VegetarianType vegetarianType,
            Gender gender,
            Integer birthYear,
            Integer height,
            Integer weight) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.vegetarianType = vegetarianType;
        this.gender = gender;
        this.birthYear = birthYear;
        this.height = height;
        this.weight = weight;
        updateDailyIntake();
    }

    private int calcAge() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        return (currentYear - birthYear);
    }

    private int calcBMR() {
        int age = calcAge();
        if (gender == Gender.F) {
            return (int) Math.round((655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)));
        } else {
            return (int) Math.round((66 + (13.7 * weight) + (5 * height) - (6.8 * age)));
        }
    }
}
