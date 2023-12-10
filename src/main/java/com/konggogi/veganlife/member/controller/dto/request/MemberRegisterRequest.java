package com.konggogi.veganlife.member.controller.dto.request;


import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.VegetarianType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record MemberRegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Length(min = 2, max = 10) String nickname,
        @NotBlank String phoneNumber,
        @NotNull Gender gender,
        @NotNull VegetarianType vegetarianType,
        @Positive @NotNull Integer birthYear,
        @Positive @NotNull Integer age,
        @Positive @NotNull Integer height,
        @Positive @NotNull Integer weight) {}
