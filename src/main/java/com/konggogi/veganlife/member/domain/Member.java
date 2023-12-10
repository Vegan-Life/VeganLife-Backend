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

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    private String profileImageUrl;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VegetarianType vegetarianType;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private Integer age;

    private Integer dailyCarbs;
    private Integer dailyProtein;
    private Integer dailyFat;
    private Integer bmr;

    @Builder
    public Member(
            String email,
            String nickname,
            String phoneNumber,
            Gender gender,
            VegetarianType vegetarianType,
            Integer birthYear,
            Integer age,
            Integer height,
            Integer weight) {
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.vegetarianType = vegetarianType;
        this.birthYear = birthYear;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.role = Role.USER;
    }

    private Integer calcBMR() {
        if (gender == Gender.F) {
            return (int) Math.round((655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)));
        } else {
            return (int) Math.round((66 + (13.7 * weight) + (5 * height) - (6.8 * age)));
        }
    }

    public void updateDailyIntake() {
        bmr = calcBMR();
        // TODO 활동량 입력 받을 경우 변경
        double amr = bmr * 1.375;
        dailyCarbs = (int) ((amr * (50.0 / 100)) / 4);
        dailyProtein = (int) ((amr * (30.0 / 100)) / 4);
        dailyFat = (int) ((amr * (20.0 / 100)) / 9);
    }
}
