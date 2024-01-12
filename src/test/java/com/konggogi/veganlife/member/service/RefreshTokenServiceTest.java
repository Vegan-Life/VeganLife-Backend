package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.mapper.MemberMapperImpl;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.fixture.RefreshTokenFixture;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock MemberQueryService memberQueryService;
    @Mock JwtProvider jwtProvider;
    @Mock JwtUtils jwtUtils;
    @Spy MemberMapper memberMapper = new MemberMapperImpl();
    @InjectMocks RefreshTokenService refreshTokenService;
    private final Member member = MemberFixture.DEFAULT_F.getWithId(1L);

    @Test
    @DisplayName("RefreshToken 저장 - 기존 토큰 업데이트")
    void updateRefreshTokenTest() {
        // given
        Long memberId = member.getId();
        RefreshToken refreshToken = RefreshTokenFixture.DEFAULT.get(memberId);
        String newToken = "new";
        given(memberQueryService.searchRefreshToken(anyLong()))
                .willReturn(Optional.of(refreshToken));
        // when
        refreshTokenService.addOrUpdate(memberId, newToken);
        // then
        assertThat(refreshToken.getToken()).isEqualTo(newToken);
        then(memberQueryService).should().searchRefreshToken(eq(memberId));
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("RefreshToken 저장 - 새로운 토큰 저장")
    void saveRefreshTokenTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.searchRefreshToken(anyLong())).willReturn(Optional.empty());
        // when
        refreshTokenService.addOrUpdate(memberId, "new");
        // then
        then(memberQueryService).should().searchRefreshToken(eq(memberId));
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("AccessToken 재발급")
    void reissueAccessTokenTest() {
        // given
        Long memberId = member.getId();
        String bearerRefreshToken = "Bearer refreshToken";
        String bearerAccessToken = "Bearer accessToken";
        RefreshToken refreshToken =
                RefreshTokenFixture.DEFAULT.getWithToken(memberId, bearerRefreshToken);
        given(jwtUtils.extractBearerToken(anyString())).willReturn(Optional.of("refreshToken"));
        doNothing().when(jwtUtils).validateToken(anyString());
        given(memberQueryService.searchByToken(anyString())).willReturn(member);
        given(memberQueryService.searchRefreshToken(anyLong()))
                .willReturn(Optional.of(refreshToken));
        given(jwtProvider.createToken(anyString())).willReturn(bearerAccessToken);
        // when
        String result = refreshTokenService.reissueAccessToken(bearerRefreshToken);
        // then
        assertThat(result).isEqualTo(bearerAccessToken);
        then(jwtProvider).should().createToken(eq(member.getEmail()));
    }

    @Test
    @DisplayName("AccessToken 재발급 - Invalid Jwt")
    void reissueAccessTokenInvalidJwtTest() {
        // given
        String bearerRefreshToken = "Bearer refreshToken";
        given(jwtUtils.extractBearerToken(anyString())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> refreshTokenService.reissueAccessToken(bearerRefreshToken))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining(ErrorCode.INVALID_TOKEN.getDescription());
        then(jwtUtils).should(never()).validateToken(anyString());
    }

    @Test
    @DisplayName("AccessToken 재발급 - Not Found RefreshToken")
    void reissueAccessTokenNotFoundTokenTest() {
        // given
        String bearerRefreshToken = "Bearer refreshToken";
        given(jwtUtils.extractBearerToken(anyString())).willReturn(Optional.of("refreshToken"));
        doNothing().when(jwtUtils).validateToken(anyString());
        given(memberQueryService.searchByToken(anyString())).willReturn(member);
        given(memberQueryService.searchRefreshToken(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> refreshTokenService.reissueAccessToken(bearerRefreshToken))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getDescription());
        then(jwtProvider).should(never()).createRefreshToken(anyString());
    }

    @Test
    @DisplayName("AccessToken 재발급 - Mismatch")
    void reissueAccessTokenMismatchTest() {
        // given
        Long memberId = member.getId();
        String bearerMismatchToken = "Bearer mismatchToken";
        String bearerRefreshToken = "Bearer refreshToken";
        RefreshToken refreshToken =
                RefreshTokenFixture.DEFAULT.getWithToken(memberId, bearerRefreshToken);
        given(jwtUtils.extractBearerToken(anyString())).willReturn(Optional.of("mismatchToken"));
        doNothing().when(jwtUtils).validateToken(anyString());
        given(memberQueryService.searchByToken(anyString())).willReturn(member);
        given(memberQueryService.searchRefreshToken(anyLong()))
                .willReturn(Optional.of(refreshToken));
        // when, then
        assertThatThrownBy(() -> refreshTokenService.reissueAccessToken(bearerMismatchToken))
                .isInstanceOf(MismatchTokenException.class)
                .hasMessageContaining(ErrorCode.MISMATCH_REFRESH_TOKEN.getDescription());
        then(jwtProvider).should(never()).createRefreshToken(anyString());
    }
}
