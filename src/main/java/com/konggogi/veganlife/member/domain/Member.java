package com.konggogi.veganlife.member.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String profileImageUrl;

    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private VegetarianType vegetarianType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private Integer height;

    private Integer weight;

    private Integer dailyCarbs;
    private Integer dailyProtein;
    private Integer dailyFat;
    private Integer AMR; // 활동 대사량

    @Column(nullable = false)
    private boolean hasAdditionalInfo;

    @Builder
    public Member(
            Long id,
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
        this.hasAdditionalInfo = false;
    }

    public int calcAge() {
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

    public void updateDailyIntake() {
        int bmr = calcBMR();
        AMR = (int) Math.round(bmr * 1.375);
        dailyCarbs = (int) ((AMR * (50.0 / 100)) / 4);
        dailyProtein = (int) ((AMR * (30.0 / 100)) / 4);
        dailyFat = (int) ((AMR * (20.0 / 100)) / 9);
    }

    public void updateMemberInfo(MemberInfoRequest memberInfoRequest) {
        this.nickname = memberInfoRequest.nickname();
        this.gender = memberInfoRequest.gender();
        this.vegetarianType = memberInfoRequest.vegetarianType();
        this.birthYear = memberInfoRequest.birthYear();
        this.height = memberInfoRequest.height();
        this.weight = memberInfoRequest.weight();
        this.hasAdditionalInfo = true;
        updateDailyIntake();
    }
}
