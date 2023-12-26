package com.konggogi.veganlife.meallog.service.dto;


import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.util.List;

public record MealLogDetails(
        MealLog mealLog,
        IntakeNutrients intakeNutrients,
        List<MealImage> mealImages,
        List<Meal> meals) {}
