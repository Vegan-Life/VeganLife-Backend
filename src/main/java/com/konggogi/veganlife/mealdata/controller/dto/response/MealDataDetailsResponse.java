package com.konggogi.veganlife.mealdata.controller.dto.response;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealDataType;

public record MealDataDetailsResponse(
        Long id,
        String name,
        MealDataType type,
        Integer amount,
        Integer amountPerServe,
        Double caloriePerUnit,
        Double carbsPerUnit,
        Double proteinPerUnit,
        Double fatPerUnit,
        IntakeUnit intakeUnit) {}
