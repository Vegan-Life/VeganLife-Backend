package com.konggogi.veganlife.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setup() {
        member = MemberFixture.DEFAULT_F.getMember();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("이메일로 Member 조회")
    void findByEmailTest() {
        // when
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("닉네임으로 Member 조회")
    void findByNicknameTest() {
        // when
        Optional<Member> findMember = memberRepository.findByNickname(member.getNickname());
        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("회원 번호로 Member 조회")
    void findByIdTest() {
        // when
        Optional<Member> findMember = memberRepository.findById(member.getId());
        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getEmail()).isEqualTo(member.getEmail());
    }
}
