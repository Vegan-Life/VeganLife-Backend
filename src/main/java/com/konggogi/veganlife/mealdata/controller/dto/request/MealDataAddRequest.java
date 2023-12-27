package com.konggogi.veganlife.mealdata.controller.dto.request;


import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MealDataAddRequest(
        @NotBlank String name,
        @NotNull Integer amount,
        @NotNull Integer amountPerServe,
        @NotNull Integer caloriePerServe,
        @NotNull Integer carbsPerServe,
        @NotNull Integer proteinPerServe,
        @NotNull Integer fatPerServe,
        @NotNull IntakeUnit intakeUnit) {}
