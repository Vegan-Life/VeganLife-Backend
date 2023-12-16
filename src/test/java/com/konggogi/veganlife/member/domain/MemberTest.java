package com.konggogi.veganlife.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.fixture.MemberFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {
    @Test
    @DisplayName("출생연도 나이 계산")
    void calcAgeTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.getMember();
        int currentYear = LocalDate.now().getYear();
        int expectedAge = currentYear - member.getBirthYear();
        // when
        int age = member.calcAge();
        // then
        assertThat(age).isEqualTo(expectedAge);
    }

    @Test
    @DisplayName("일일 섭취량 업데이트")
    void updateDailyIntakeTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.getMemberWithoutNutrientInfo();
        int age = LocalDate.now().getYear() - member.getBirthYear();
        int weight = member.getWeight();
        int height = member.getHeight();
        int BMR = (int) Math.round((66 + (13.7 * weight) + (5 * height) - (6.8 * age)));
        int expectedAMR = (int) Math.round(BMR * 1.375);
        int expectedCarbs = (int) ((expectedAMR * (50.0 / 100)) / 4);
        int expectedProtein = (int) ((expectedAMR * (30.0 / 100)) / 4);
        int expectedFat = (int) ((expectedAMR * (20.0 / 100)) / 9);
        // when
        member.updateDailyIntake();
        // then
        assertThat(member.getAMR()).isEqualTo(expectedAMR);
        assertThat(member.getDailyCarbs()).isEqualTo(expectedCarbs);
        assertThat(member.getDailyProtein()).isEqualTo(expectedProtein);
        assertThat(member.getDailyFat()).isEqualTo(expectedFat);
    }

    @Test
    @DisplayName("회원 정보 업데이트")
    void updateMemberInfoTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.getMemberWithoutInfo();
        String nickname = "닉네임";
        Gender gender = Gender.F;
        VegetarianType vegetarianType = VegetarianType.LACTO_OVO;
        Integer birthYear = 2005;
        Integer height = 170;
        Integer weight = 55;
        // when
        member.updateMemberInfo(nickname, gender, vegetarianType, birthYear, height, weight);
        // then
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getGender()).isEqualTo(gender);
        assertThat(member.getVegetarianType()).isEqualTo(vegetarianType);
        assertThat(member.getBirthYear()).isEqualTo(birthYear);
        assertThat(member.getHeight()).isEqualTo(height);
        assertThat(member.getWeight()).isEqualTo(weight);
        assertThat(member.isHasAdditionalInfo()).isTrue();
        assertThat(member.getAMR()).isNotNull();
        assertThat(member.getDailyCarbs()).isNotNull();
        assertThat(member.getDailyProtein()).isNotNull();
        assertThat(member.getDailyFat()).isNotNull();
    }
}
