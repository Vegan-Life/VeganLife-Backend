package com.konggogi.veganlife.meallog.service.dto;

public record MealListDto(
        Long id, Integer intake, Integer calorie, Integer carbs, Integer protein, Integer fat) {}
