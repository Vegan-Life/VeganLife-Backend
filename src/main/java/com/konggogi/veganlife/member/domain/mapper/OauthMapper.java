package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OauthMapper {
    OauthLoginResponse toOauthLoginResponse(Member member);
}
