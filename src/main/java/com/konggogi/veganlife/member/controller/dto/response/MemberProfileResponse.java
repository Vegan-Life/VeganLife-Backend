package com.konggogi.veganlife.member.controller.dto.response;


import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.VegetarianType;

public record MemberProfileResponse(
        String email,
        String nickname,
        VegetarianType vegetarianType,
        Gender gender,
        String imageUrl,
        Integer height,
        Integer weight) {}
