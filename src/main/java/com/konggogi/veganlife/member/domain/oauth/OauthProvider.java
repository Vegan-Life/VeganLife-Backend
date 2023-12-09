package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import lombok.Getter;

@Getter
public enum OauthProvider {
    KAKAO("kakao"),
    NAVER("naver");

    private final String provider;

    OauthProvider(String provider) {
        this.provider = provider;
    }

    public static OauthProvider from(String provider) {
        for (OauthProvider oauthProvider : OauthProvider.values()) {
            if (oauthProvider.getProvider().equals(provider)) {
                return oauthProvider;
            }
        }
        throw new UnsupportedProviderException(ErrorCode.UNSUPPORTED_PROVIDER);
    }
}
