package com.konggogi.veganlife.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {
    @Test
    @DisplayName("추가 정보 업데이트")
    void updateMemberInfoTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.getOnlyEmailWithId(1L);
        Member expectedMember = MemberFixture.DEFAULT_F.get();
        // when
        member.updateAdditionalInfo(
                expectedMember.getNickname(),
                expectedMember.getGender(),
                expectedMember.getVegetarianType(),
                expectedMember.getBirthYear(),
                expectedMember.getHeight(),
                expectedMember.getWeight());
        // then
        assertThat(member.getNickname()).isEqualTo(expectedMember.getNickname());
        assertThat(member.getGender()).isEqualTo(expectedMember.getGender());
        assertThat(member.getVegetarianType()).isEqualTo(expectedMember.getVegetarianType());
        assertThat(member.getBirthYear()).isEqualTo(expectedMember.getBirthYear());
        assertThat(member.getHeight()).isEqualTo(expectedMember.getHeight());
        assertThat(member.getWeight()).isEqualTo(expectedMember.getWeight());
        assertThat(member.isHasAdditionalInfo()).isTrue();
        assertThat(member.getAMR()).isEqualTo(expectedMember.getAMR());
        assertThat(member.getDailyCarbs()).isEqualTo(expectedMember.getDailyCarbs());
        assertThat(member.getDailyProtein()).isEqualTo(expectedMember.getDailyProtein());
        assertThat(member.getDailyFat()).isEqualTo(expectedMember.getDailyFat());
    }

    @Test
    @DisplayName("회원 프로필 수정")
    void modifyMemberProfileTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.getWithId(1L);
        String nickname = "닉네임";
        String profileImageUrl = "imageUrl";
        Gender gender = Gender.F;
        VegetarianType vegetarianType = VegetarianType.LACTO_OVO;
        Integer birthYear = 2005;
        Integer height = 170;
        Integer weight = 55;
        // when
        member.modifyProfile(
                nickname, profileImageUrl, vegetarianType, gender, birthYear, height, weight);
        // then
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getGender()).isEqualTo(gender);
        assertThat(member.getVegetarianType()).isEqualTo(vegetarianType);
        assertThat(member.getBirthYear()).isEqualTo(birthYear);
        assertThat(member.getHeight()).isEqualTo(height);
        assertThat(member.getWeight()).isEqualTo(weight);
    }
}
