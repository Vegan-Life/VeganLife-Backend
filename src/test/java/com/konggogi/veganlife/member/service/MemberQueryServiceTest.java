package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
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
    @Mock JwtUtils jwtUtils;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @InjectMocks MemberQueryService memberQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("회원 Id로 Member 조회")
    void searchByIdTest() {
        // given
        Long memberId = member.getId();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        // when
        Member result = memberQueryService.search(memberId);
        // then
        assertThat(result).isEqualTo(member);
        then(memberRepository).should().findById(memberId);
    }

    @Test
    @DisplayName("회원 Id로 Member 조회 - Not Found Member")
    void searchByIdNotFoundMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberQueryService.search(memberId))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberRepository).should().findById(eq(memberId));
    }

    @Test
    @DisplayName("회원 email로 Member 조회")
    void searchByEmailTest() {
        // given
        String email = member.getEmail();
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        // when
        Member result = memberQueryService.searchByEmail(email);
        // then
        assertThat(result).isEqualTo(member);
        then(memberRepository).should().findByEmail(eq(email));
    }

    @Test
    @DisplayName("회원 email로 Member 조회 - Not Found Member")
    void searchByEmailNotFoundMemberTest() {
        // given
        String email = member.getEmail();
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberQueryService.searchByEmail(email))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberRepository).should().findByEmail(eq(email));
    }

    @Test
    @DisplayName("회원 Id로 RefreshToken 조회")
    void searchRefreshToken() {
        // given
        Long memberId = member.getId();
        // when, then
        assertThatNoException().isThrownBy(() -> memberQueryService.searchRefreshToken(memberId));
        then(refreshTokenRepository).should().findByMemberId(eq(memberId));
    }

    @Test
    @DisplayName("토큰으로 Member 조회")
    void searchByTokenTest() {
        // given
        String email = member.getEmail();
        given(jwtUtils.extractUserEmail(anyString())).willReturn(Optional.of(email));
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        // when
        Member result = memberQueryService.searchByToken("token");
        // then
        assertThat(result).isEqualTo(member);
        then(memberRepository).should().findByEmail(eq(email));
    }

    @Test
    @DisplayName("토큰으로 Member 조회 - Invalid Jwt")
    void searchByTokenInvalidJwtTest() {
        // given
        given(jwtUtils.extractUserEmail(anyString())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberQueryService.searchByToken("token"))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_USER_INFO_TOKEN.getDescription());
        then(memberRepository).should(never()).findByEmail(eq(member.getEmail()));
    }
}
