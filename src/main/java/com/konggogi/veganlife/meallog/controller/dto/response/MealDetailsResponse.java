package com.konggogi.veganlife.meallog.controller.dto.response;


import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;

public record MealDetailsResponse(Long id, Integer intake, MealDataDetailsResponse mealData) {}
