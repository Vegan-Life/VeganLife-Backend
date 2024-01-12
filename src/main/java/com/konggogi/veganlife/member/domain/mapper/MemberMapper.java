package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.controller.dto.response.MemberInfoResponse;
import com.konggogi.veganlife.member.controller.dto.response.MemberProfileResponse;
import com.konggogi.veganlife.member.controller.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member toMember(String email);

    RefreshToken toRefreshToken(Long memberId, String token);

    OauthLoginResponse toOauthLoginResponse(Member member, String accessToken, String refreshToken);

    @Mapping(target = "imageUrl", source = "member.profileImageUrl")
    MemberProfileResponse toMemberProfileResponse(Member member);

    MemberInfoResponse toMemberInfoResponse(Member member);
}
