package com.konggogi.veganlife.member.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
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

    @Column(nullable = false, unique = true)
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
            String email,
            String nickname,
            Gender gender,
            VegetarianType vegetarianType,
            Integer birthYear,
            Integer height,
            Integer weight) {
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.vegetarianType = vegetarianType;
        this.birthYear = birthYear;
        this.height = height;
        this.weight = weight;
        this.role = Role.USER;
    }

    public Integer calcAge() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        return (currentYear - birthYear);
    }

    private Integer calcBMR() {
        Integer age = calcAge();
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
}
