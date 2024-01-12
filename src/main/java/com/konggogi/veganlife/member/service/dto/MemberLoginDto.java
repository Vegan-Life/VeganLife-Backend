package com.konggogi.veganlife.member.service.dto;


import com.konggogi.veganlife.member.domain.Member;

public record MemberLoginDto(Member member, String accessToken, String refreshToken) {}
