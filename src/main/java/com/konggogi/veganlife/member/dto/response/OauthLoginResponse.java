package com.konggogi.veganlife.member.dto.response;


import lombok.Builder;

@Builder
public record OauthLoginResponse(boolean isSigned, String accessToken, String refreshToken) {
    public static OauthLoginResponse from(
            boolean isSigned, String accessToken, String refreshToken) {
        return OauthLoginResponse.builder()
                .isSigned(isSigned)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
