package com.konggogi.veganlife.member.controller.dto.request;


import jakarta.validation.constraints.NotNull;

public record OauthLoginRequest(@NotNull String accessToken) {}
