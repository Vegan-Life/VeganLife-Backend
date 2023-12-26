package com.konggogi.veganlife.meallog.controller.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MealModifyRequest(
        @NotNull @Min(0) Integer intake,
        @NotNull @Min(0) Integer calorie,
        @NotNull @Min(0) Integer carbs,
        @NotNull @Min(0) Integer protein,
        @NotNull @Min(0) Integer fat,
        @NotNull @Min(1L) Long mealDataId) {}
