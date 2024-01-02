package com.konggogi.veganlife.member.service.dto;


import com.konggogi.veganlife.member.domain.oauth.OauthProvider;

public record UserInfo(OauthProvider provider, String id, String email) {}
