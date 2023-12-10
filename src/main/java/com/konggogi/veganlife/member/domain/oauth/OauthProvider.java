package com.konggogi.veganlife.member.domain.oauth;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.exception.UnsupportedProviderException;
import lombok.Getter;

@Getter
public enum OauthProvider {
    KAKAO,
    NAVER;

    public static OauthProvider from(String provider) {
        String providerUpperCase = provider.toUpperCase();
        for (OauthProvider oauthProvider : OauthProvider.values()) {
            if (oauthProvider.name().equals(providerUpperCase)) {
                return oauthProvider;
            }
        }
        throw new UnsupportedProviderException(ErrorCode.UNSUPPORTED_PROVIDER);
    }
}
