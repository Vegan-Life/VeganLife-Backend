package com.konggogi.veganlife.member.domain.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OauthProviderTest {
    @Test
    @DisplayName("문자열을 OauthProvier로 매칭")
    void fromTest() {
        // given
        String provider = "kakao";
        // when
        OauthProvider newOauthProvider = OauthProvider.from(provider);
        // then
        assertThat(newOauthProvider).isEqualTo(OauthProvider.KAKAO);
    }

    @Test
    @DisplayName("매칭되는 OauthProvier가 없으면 예외 발생")
    void fromUnsupportedProviderTest() {
        // given
        String provider = "none";
        // when, then
        assertThatThrownBy(() -> OauthProvider.from(provider))
                .isInstanceOf(UnsupportedProviderException.class)
                .hasMessageContaining(ErrorCode.UNSUPPORTED_PROVIDER.getDescription());
    }
}
