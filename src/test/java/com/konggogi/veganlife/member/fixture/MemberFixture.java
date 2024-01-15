package com.konggogi.veganlife.member.fixture;


import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;

public enum MemberFixture {
    DEFAULT_F(
            "testF@test.com",
            "비건라이프F",
            "https:/s3/images/image.png",
            2000,
            Gender.F,
            VegetarianType.VEGAN,
            165,
            50,
            226,
            136,
            40,
            1814),
    DEFAULT_M(
            "testM@test.com",
            "비건라이프M",
            "https:/s3/images/image.png",
            1997,
            Gender.M,
            VegetarianType.OVO,
            185,
            85,
            339,
            203,
            60,
            2721);
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final Integer birthYear;
    private final Gender gender;
    private final VegetarianType vegetarianType;
    private final Integer height;
    private final Integer weight;
    private final Integer dailyCarbs;
    private final Integer dailyProtein;
    private final Integer dailyFat;
    private final Integer AMR;

    MemberFixture(
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
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.birthYear = birthYear;
        this.gender = gender;
        this.vegetarianType = vegetarianType;
        this.height = height;
        this.weight = weight;
        this.dailyCarbs = dailyCarbs;
        this.dailyProtein = dailyProtein;
        this.dailyFat = dailyFat;
        this.AMR = AMR;
    }

    public Member get() {
        return createCommonMemberBuilder()
                .dailyCarbs(dailyCarbs)
                .dailyProtein(dailyProtein)
                .dailyFat(dailyFat)
                .AMR(AMR)
                .build();
    }

    public Member getWithId(Long id) {
        return createCommonMemberBuilder()
                .id(id)
                .dailyCarbs(dailyCarbs)
                .dailyProtein(dailyProtein)
                .dailyFat(dailyFat)
                .AMR(AMR)
                .build();
    }

    public Member getOnlyEmailWithId(Long id) {
        return Member.builder().id(id).email(email).build();
    }

    public Member getWithoutNutrientInfo() {
        return createCommonMemberBuilder().build();
    }

    private Member.MemberBuilder createCommonMemberBuilder() {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .birthYear(birthYear)
                .gender(gender)
                .vegetarianType(vegetarianType)
                .height(height)
                .weight(weight);
    }
}
