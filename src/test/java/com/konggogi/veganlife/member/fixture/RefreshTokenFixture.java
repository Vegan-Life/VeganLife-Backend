package com.konggogi.veganlife.member.fixture;


import com.konggogi.veganlife.global.security.jwt.RefreshToken;

public enum RefreshTokenFixture {
    DEFAULT("token");
    private final String token;

    RefreshTokenFixture(String token) {
        this.token = token;
    }

    public RefreshToken get(Long memberId) {
        return RefreshToken.builder().memberId(memberId).token(token).build();
    }

    public RefreshToken getWithToken(Long memberId, String token) {
        return RefreshToken.builder().memberId(memberId).token(token).build();
    }

    public RefreshToken getWithId(Long id, Long memberId) {
        return RefreshToken.builder().id(id).memberId(memberId).token(token).build();
    }

    public RefreshToken getWithIdAndToken(Long id, Long memberId, String token) {
        return RefreshToken.builder().id(id).memberId(memberId).token(token).build();
    }
}
