package com.konggogi.veganlife.meallog.controller.dto.response;


import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.util.List;

public record MealLogDetailsResponse(
        Long id,
        MealType mealType,
        IntakeNutrients totalIntakeNutrients,
        List<String> imageUrls,
        List<MealDetailsResponse> meals) {}
