package com.konggogi.veganlife.meallog.controller.dto.response;


import com.konggogi.veganlife.meallog.domain.MealType;

public record MealLogListResponse(
        Long id, MealType mealType, String thumbnailUrl, Integer totalCalorie) {}
