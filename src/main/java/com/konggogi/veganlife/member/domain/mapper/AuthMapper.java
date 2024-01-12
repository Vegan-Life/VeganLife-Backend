package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.response.ReissueTokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    ReissueTokenResponse toReissueTokenResponse(String accessToken);
}
