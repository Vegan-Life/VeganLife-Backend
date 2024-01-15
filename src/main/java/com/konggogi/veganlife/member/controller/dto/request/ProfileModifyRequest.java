package com.konggogi.veganlife.member.controller.dto.request;


import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.VegetarianType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record ProfileModifyRequest(
        @NotBlank @Length(min = 2, max = 10) String nickname,
        @NotNull String imageUrl,
        @NotNull VegetarianType vegetarianType,
        @NotNull Gender gender,
        @Positive @NotNull Integer birthYear,
        @Positive @NotNull Integer height,
        @Positive @NotNull Integer weight) {}
