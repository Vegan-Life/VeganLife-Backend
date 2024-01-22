package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.controller.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.controller.dto.response.ReissueTokenResponse;
import com.konggogi.veganlife.member.service.dto.MemberLoginDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "id", ignore = true)
    RefreshToken toRefreshToken(Long memberId, String token);

    ReissueTokenResponse toReissueTokenResponse(String accessToken, String refreshToken);

    @Mapping(target = "email", source = "memberLoginDto.member.email")
    @Mapping(target = "hasAdditionalInfo", source = "memberLoginDto.member.hasAdditionalInfo")
    OauthLoginResponse toOauthLoginResponse(MemberLoginDto memberLoginDto);
}
