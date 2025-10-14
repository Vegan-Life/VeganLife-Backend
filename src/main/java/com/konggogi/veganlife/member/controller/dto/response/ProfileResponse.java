package com.konggogi.veganlife.member.controller.dto.response;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;

public record ProfileResponse(
        Long id, String nickname, String profileImageUrl, VegetarianType vegetarianType) {

    public static ProfileResponse from(Member member) {
        return new ProfileResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getVegetarianType());
    }
}
