package com.konggogi.veganlife.meallog.controller.dto.response;


import com.konggogi.veganlife.mealdata.domain.MealDataType;
import lombok.Builder;

@Builder
public record MealDetailsResponse(
        String name,
        Integer intake,
        MealDataType mealDataType,
        Integer amount,
        Integer amountPerServe) {}
