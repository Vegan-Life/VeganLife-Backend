package com.konggogi.veganlife.member.controller.dto.response;

public record OauthLoginResponse(String email, String accessToken, String refreshToken) {}
