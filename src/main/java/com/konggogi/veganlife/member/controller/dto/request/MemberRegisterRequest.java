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
        @NotNull int birthYear,
        @NotNull int age,
        @NotNull int height,
        @NotNull int weight) {}
