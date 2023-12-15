package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.domain.Member;
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
class MemberQueryServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock private JwtUtils jwtUtils;
    @Mock private JwtProvider jwtProvider;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks MemberQueryService memberQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getMember();

    @Test
    @DisplayName("회원 번호로 조회")
    void searchTest() {
        // given
        Long memberId = member.getId();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        // when
        Member foundMember = memberQueryService.search(memberId);
        // then
        then(memberRepository).should().findById(memberId);
        assertThat(foundMember).isEqualTo(member);
    }

    @Test
    @DisplayName("없는 회원 번호로 조회 시 예외 발생")
    void searchNotMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberQueryService.search(memberId))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberRepository).should().findById(memberId);
    }

    @Test
    @DisplayName("AccessToken 재발급")
    void reissueTokenTest() {
        // given
        Long memberId = member.getId();
        String email = member.getEmail();
        String token = "refreshToken";
        String bearerToken = JwtUtils.BEARER_PREFIX + token;
        String accessToken = "accessToken";
        RefreshToken refreshToken = new RefreshToken(bearerToken, memberId);
        doNothing().when(jwtUtils).validateToken(token);
        given(jwtUtils.extractUserEmail(token)).willReturn(Optional.of(email));
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(refreshTokenRepository.findRefreshTokenByMemberId(memberId))
                .willReturn(Optional.of(refreshToken));
        given(jwtProvider.createToken(email)).willReturn(accessToken);
        // when
        String reissueToken = memberQueryService.reissueToken(token);
        // then
        assertThat(reissueToken).isEqualTo(accessToken);
        then(refreshTokenRepository).should().findRefreshTokenByMemberId(memberId);
        then(jwtProvider).should().createToken(email);
    }

    @Test
    @DisplayName("AccessToken 재발급 시 RefreshToken을 찾지 못하면 예외 발생")
    void reissueTokenNotFoundRefreshTokenTest() {
        // given
        Long memberId = member.getId();
        String email = member.getEmail();
        String token = "refreshToken";
        doNothing().when(jwtUtils).validateToken(token);
        given(jwtUtils.extractUserEmail(token)).willReturn(Optional.of(email));
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(refreshTokenRepository.findRefreshTokenByMemberId(memberId))
                .willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberQueryService.reissueToken(token))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getDescription());
        then(refreshTokenRepository).should().findRefreshTokenByMemberId(memberId);
        then(jwtProvider).should(never()).createToken(email);
    }

    @Test
    @DisplayName("AccessToken 재발급 시 찾은 RefreshToken과 불일치하면 예외 발생")
    void reissueTokenMismatchTest() {
        // given
        Long memberId = member.getId();
        String email = member.getEmail();
        String token = "refreshToken";
        String bearerToken = JwtUtils.BEARER_PREFIX + "mismatch" + token;
        RefreshToken refreshToken = new RefreshToken(bearerToken, memberId);
        doNothing().when(jwtUtils).validateToken(token);
        given(jwtUtils.extractUserEmail(token)).willReturn(Optional.of(email));
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(refreshTokenRepository.findRefreshTokenByMemberId(memberId))
                .willReturn(Optional.of(refreshToken));
        // when, then
        assertThatThrownBy(() -> memberQueryService.reissueToken(token))
                .isInstanceOf(MismatchTokenException.class)
                .hasMessageContaining(ErrorCode.MISMATCH_REFRESH_TOKEN.getDescription());
        then(refreshTokenRepository).should().findRefreshTokenByMemberId(memberId);
        then(jwtProvider).should(never()).createToken(email);
    }

    @Test
    @DisplayName("회원 번호로 회원 찾기")
    void findMemberByIdTest() {
        // given
        Long memberId = member.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        // when
        Member foundMember = memberQueryService.findMemberById(memberId);
        // then
        then(memberRepository).should().findById(memberId);
        assertThat(foundMember).isEqualTo(member);
    }

    @Test
    @DisplayName("회원 번호로 회원 찾지 못하면 예외 발생")
    void findNotMemberByIdTest() {
        // given
        Long memberId = member.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberQueryService.findMemberById(memberId))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberRepository).should().findById(memberId);
    }
}