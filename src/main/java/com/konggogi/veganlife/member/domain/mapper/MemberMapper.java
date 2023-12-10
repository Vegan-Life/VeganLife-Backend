package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.request.MemberRegisterRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member from(MemberRegisterRequest registerRequest, Role role);
}
