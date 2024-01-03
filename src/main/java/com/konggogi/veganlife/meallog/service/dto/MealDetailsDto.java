package com.konggogi.veganlife.meallog.service.dto;

public record MealDetailsDto(
        Long id,
        Integer intake,
        Integer calorie,
        Integer carbs,
        Integer protein,
        Integer fat,
        MealDataDetailsDto mealData) {}
