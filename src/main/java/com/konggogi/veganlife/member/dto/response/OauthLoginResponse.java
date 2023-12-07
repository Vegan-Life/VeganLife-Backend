package com.konggogi.veganlife.member.dto.response;


import lombok.Builder;

@Builder
public record OauthLoginResponse(boolean isSigned, String accessToken, String refreshToken) {}
