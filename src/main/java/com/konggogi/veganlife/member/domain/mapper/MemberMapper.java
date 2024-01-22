package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.response.AdditionalInfoUpdateResponse;
import com.konggogi.veganlife.member.controller.dto.response.MemberProfileResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.MemberLoginDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    default Member toMember(String email) {
        return Member.builder().email(email).build();
    }

    MemberLoginDto toMemberLoginDto(Member member, String accessToken, String refreshToken);

    @Mapping(target = "imageUrl", source = "member.profileImageUrl")
    MemberProfileResponse toMemberProfileResponse(Member member);

    AdditionalInfoUpdateResponse toAdditionalInfoUpdateResponse(Member member);
}
