package com.konggogi.veganlife.mealdata.controller.dto.response;


import com.konggogi.veganlife.mealdata.domain.AccessType;

public record MealDataListResponse(Long id, String name, AccessType accessType) {}
