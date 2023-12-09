package com.konggogi.veganlife.member.domain.oauth;


import lombok.Getter;

@Getter
public enum OauthProvider {
    KAKAO("kakao"),
    NAVER("naver");

    private final String provider;

    OauthProvider(String provider) {
        this.provider = provider;
    }
}
