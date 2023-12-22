package com.konggogi.veganlife.meallog.controller.dto.request;


import com.konggogi.veganlife.meallog.domain.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MealLogAddRequest(
        @NotNull MealType mealType,
        @Valid @NotNull @Size(min = 1, max = 5) List<MealAddRequest> meals,
        @Size(max = 5) List<String> imageUrls) {}
