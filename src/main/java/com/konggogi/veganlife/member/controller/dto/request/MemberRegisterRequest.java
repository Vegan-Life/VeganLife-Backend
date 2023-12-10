package com.konggogi.veganlife.member.controller.dto.request;


import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.VegetarianType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRegisterRequest(
        @NotBlank String email,
        @NotBlank String nickname,
        @NotBlank String phoneNumber,
        @NotNull Gender gender,
        @NotNull VegetarianType vegetarianType,
        @NotNull Integer birthYear,
        @NotNull Integer age,
        @NotNull Integer height,
        @NotNull Integer weight) {}
