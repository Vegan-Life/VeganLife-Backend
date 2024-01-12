package com.konggogi.veganlife.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.fixture.RefreshTokenFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RefreshTokenRepositoryTest {
    @Autowired RefreshTokenRepository refreshTokenRepository;
    private final Long memberId = 1L;
    private final RefreshToken refreshToken = RefreshTokenFixture.DEFAULT.get(memberId);

    @BeforeEach
    void setup() {
        refreshTokenRepository.save(refreshToken);
    }

    @Test
    @DisplayName("회원 Id로 RefreshToken 조회")
    void findByMemberIdTest() {
        // when
        Optional<RefreshToken> result = refreshTokenRepository.findByMemberId(memberId);
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMemberId()).isEqualTo(memberId);
    }
}
