package com.konggogi.veganlife.meallog.controller.dto.request;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MealAddRequest(
        @NotBlank String name,
        @NotNull @Min(0) Integer intake,
        @NotNull IntakeUnit intakeUnit,
        @NotNull @Min(0) Integer calorie,
        @NotNull @Min(0) Integer carbs,
        @NotNull @Min(0) Integer protein,
        @NotNull @Min(0) Integer fat,
        @NotNull Long mealDataId) {}
