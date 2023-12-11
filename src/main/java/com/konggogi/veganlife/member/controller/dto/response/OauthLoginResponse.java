package com.konggogi.veganlife.member.controller.dto.response;

public record OauthLoginResponse(
        boolean hasAdditionalInfo, String email, String accessToken, String refreshToken) {}
