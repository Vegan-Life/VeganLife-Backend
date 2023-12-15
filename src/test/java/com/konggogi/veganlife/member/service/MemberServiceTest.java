package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.exception.DuplicateNicknameException;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock MemberQueryService memberQueryService;
    @InjectMocks MemberService memberService;
    private final Member member = MemberFixture.DEFAULT_F.getMemberWithoutInfo();

    @Test
    @DisplayName("이미 가입한 회원인 경우 기존 Member 반환")
    void addExistingMemberTest() {
        // given
        String email = member.getEmail();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        // when
        Member existingMember = memberService.addMember(email);
        // then
        assertThat(existingMember).isEqualTo(member);
        then(memberRepository).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("처음 가입할 경우 Member 생성 및 저장")
    void addNewMemberTest() {
        // given
        String email = member.getEmail();
        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(member);
        // when
        Member newMember = memberService.addMember(email);
        // then
        assertThat(member).isEqualTo(newMember);
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("회원 삭제")
    void removeMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.findMemberById(memberId)).willReturn(member);
        // when
        memberService.removeMember(memberId);
        // then
        then(memberRepository).should().delete(member);
    }

    @Test
    @DisplayName("회원 정보 수정")
    void modifyMemberInfoTest() {
        // given
        Long memberId = member.getId();
        MemberInfoRequest request =
                new MemberInfoRequest("테스트유저", Gender.M, VegetarianType.LACTO, 1990, 180, 83);
        given(memberRepository.findByNickname(request.nickname())).willReturn(Optional.empty());
        given(memberQueryService.findMemberById(memberId)).willReturn(member);
        // when
        Member updatedMember = memberService.modifyMemberInfo(memberId, request);
        // then
        assertThat(request.nickname()).isEqualTo(updatedMember.getNickname());
        assertThat(request.gender()).isEqualTo(updatedMember.getGender());
        assertThat(request.vegetarianType()).isEqualTo(updatedMember.getVegetarianType());
        assertThat(request.birthYear()).isEqualTo(updatedMember.getBirthYear());
        assertThat(request.height()).isEqualTo(updatedMember.getHeight());
        assertThat(request.weight()).isEqualTo(updatedMember.getWeight());
        then(memberRepository).should().findByNickname(request.nickname());
    }

    @Test
    @DisplayName("중복된 닉네임으로 인한 회원 정보 수정 예외 발생")
    void modifyMemberInfoFailTest() {
        // given
        Long memberId = member.getId();
        Member existingMember = MemberFixture.DEFAULT_F.getMember();
        String nickname = existingMember.getNickname();
        MemberInfoRequest request =
                new MemberInfoRequest(nickname, Gender.F, VegetarianType.VEGAN, 2000, 165, 50);
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.of(existingMember));
        // when, then
        assertThatThrownBy(() -> memberService.modifyMemberInfo(memberId, request))
                .isInstanceOf(DuplicateNicknameException.class);
        then(memberRepository).should().findByNickname(nickname);
        then(memberRepository).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("기존 RefreshToken 업데이트")
    void updateRefreshTokenTest() {
        // given
        Long memberId = member.getId();
        String oldToken = "oldRefreshToken";
        String newToken = "newRefreshToken";
        RefreshToken refreshToken = new RefreshToken(oldToken, memberId);
        given(refreshTokenRepository.findRefreshTokenByMemberId(memberId))
                .willReturn(Optional.of(refreshToken));
        // when
        memberService.saveRefreshToken(memberId, newToken);
        // then
        then(refreshTokenRepository).should().findRefreshTokenByMemberId(memberId);
        then(memberRepository).should(never()).save(any(Member.class));
        assertThat(refreshToken.getToken()).isEqualTo(newToken);
    }

    @Test
    @DisplayName("새로 RefreshToken 저장")
    void saveRefreshTokenTest() {
        // given
        Long memberId = member.getId();
        String newToken = "newRefreshToken";
        given(refreshTokenRepository.findRefreshTokenByMemberId(memberId))
                .willReturn(Optional.empty());
        // when
        memberService.saveRefreshToken(memberId, newToken);
        // then
        then(refreshTokenRepository).should().findRefreshTokenByMemberId(memberId);
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }
}