package com.konggogi.veganlife.meallog.controller.dto.request;


import com.konggogi.veganlife.meallog.domain.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MealLogAddRequest(
        @NotNull MealType mealType, @Size(min = 1) List<MealAddRequest> mealAddRequests) {}
