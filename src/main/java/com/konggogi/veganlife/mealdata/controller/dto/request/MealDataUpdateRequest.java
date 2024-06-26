package com.konggogi.veganlife.mealdata.controller.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MealDataUpdateRequest(
        @NotBlank String name,
        @NotNull Integer amount,
        @NotNull Integer amountPerServe,
        @NotNull Integer caloriePerServe,
        @NotNull Integer carbsPerServe,
        @NotNull Integer proteinPerServe,
        @NotNull Integer fatPerServe,
        @NotNull String intakeUnit) {}
