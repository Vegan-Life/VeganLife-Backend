package com.konggogi.veganlife.meallog.controller.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MealLogModifyRequest(
        @Valid @NotNull @Size(min = 1, max = 5) List<MealAddRequest> meals,
        @NotNull @Size(max = 5) List<String> imageUrls) {}
