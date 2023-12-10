package com.konggogi.veganlife.member.dto.response;


import com.konggogi.veganlife.member.domain.Member;
import lombok.Builder;

@Builder
public record OauthLoginResponse(String email, String birthYear, String phoneNumber) {
    public static OauthLoginResponse from(Member member) {
        return OauthLoginResponse.builder()
                .email(member.getEmail())
                .birthYear(member.getBirthYear())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}
