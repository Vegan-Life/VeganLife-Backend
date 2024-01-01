package com.konggogi.veganlife.member.fixture;


import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;

public enum MemberFixture {
    DEFAULT_F(
            "테스트유저",
            "image1.jpg",
            2000,
            Gender.F,
            VegetarianType.VEGAN,
            165,
            50,
            227,
            136,
            40,
            1821),
    DEFAULT_M(
            "테스트유저", "image1.jpg", 1997, Gender.M, VegetarianType.OVO, 185, 85, 340, 204, 60, 2721);
    private Long id = 1L;
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

    public Member getMember() {
        Member.MemberBuilder builder = createCommonMemberBuilder();
        return builder.dailyCarbs(dailyCarbs)
                .dailyProtein(dailyProtein)
                .dailyFat(dailyFat)
                .AMR(AMR)
                .build();
    }

    public Member getMemberWithName(String nickname) {
        Member.MemberBuilder builder = createCommonMemberBuilder();
        return builder.nickname(nickname)
                .dailyCarbs(dailyCarbs)
                .dailyProtein(dailyProtein)
                .dailyFat(dailyFat)
                .AMR(AMR)
                .build();
    }

    public Member getMemberWithoutInfo() {
        Long id = this.id++;
        String email = "test" + id + "@test.com";
        return Member.builder().id(id).email(email).build();
    }

    public Member getMemberWithoutNutrientInfo() {
        return createCommonMemberBuilder().build();
    }

    private Member.MemberBuilder createCommonMemberBuilder() {
        Long id = this.id++;
        String nickname = this.nickname + id;
        String email = "test" + id + "@test.com";
        return Member.builder()
                .id(id)
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
