package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.request.SignupRequest;
import com.konggogi.veganlife.member.controller.dto.response.JwtTokenResponse;
import com.konggogi.veganlife.member.controller.dto.response.MemberProfileResponse;
import com.konggogi.veganlife.member.controller.dto.response.RecommendNutrientsResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.JwtToken;
import com.konggogi.veganlife.member.service.dto.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    JwtTokenResponse toJwtTokenResponse(JwtToken jwtToken);

    @Mapping(target = "imageUrl", source = "member.profileImageUrl")
    MemberProfileResponse toMemberProfileResponse(Member member);

    @Mapping(target = "dailyCalorie", source = "member.AMR")
    RecommendNutrientsResponse toRecommendNutrientsResponse(Member member);

    default Member toEntity(UserInfo userInfo, SignupRequest request) {

        Member member =
                Member.builder()
                        .oauthProvider(userInfo.provider())
                        .oauthUserId(userInfo.id())
                        .email(userInfo.email())
                        .nickname(request.nickname())
                        .gender(request.gender())
                        .vegetarianType(request.vegetarianType())
                        .birthYear(request.birthYear())
                        .height(request.height())
                        .weight(request.weight())
                        .build();
        member.updateDailyIntake();
        return member;
    }
}
