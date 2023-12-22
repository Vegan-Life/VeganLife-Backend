package com.konggogi.veganlife.meallog.service.dto;


import com.konggogi.veganlife.meallog.domain.MealType;

public record MealLogList(MealType mealType, String thumbnailUrl, Integer totalCalorie) {}
