package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.request.MemberRegisterRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberRegisterResponse;
import com.konggogi.veganlife.member.controller.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member toEntity(MemberRegisterRequest registerRequest);

    MemberRegisterResponse toMemberRegisterResponse(
            Member member, String accessToken, String refreshToken);

    OauthLoginResponse toOauthLoginResponse(Member member);
}
