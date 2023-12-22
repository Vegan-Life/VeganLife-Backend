package com.konggogi.veganlife.meallog.service.dto;


import com.konggogi.veganlife.meallog.domain.MealLog;

public record MealLogList(MealLog mealLog, String thumbnailUrl, Integer totalCalorie) {}
