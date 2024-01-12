package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.mapper.MemberMapperImpl;
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
    @Spy MemberMapper memberMapper = new MemberMapperImpl();
    @InjectMocks RefreshTokenService refreshTokenService;
    private final Long memberId = 1L;

    @Test
    @DisplayName("RefreshToken 저장 - 기존 토큰 업데이트")
    void updateRefreshTokenTest() {
        // given
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
        given(memberQueryService.searchRefreshToken(anyLong())).willReturn(Optional.empty());
        // when
        refreshTokenService.addOrUpdate(memberId, "new");
        // then
        then(memberQueryService).should().searchRefreshToken(eq(memberId));
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }
}
