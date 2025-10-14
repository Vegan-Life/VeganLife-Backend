package com.konggogi.veganlife.meallog.controller.dto.response;


import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;

public record MealLogListResponse(
        Long id, MealType mealType, String thumbnailUrl, Integer totalCalorie) {

    public static MealLogListResponse from(MealLog mealLog) {
        return new MealLogListResponse(
                mealLog.getId(),
                mealLog.getMealType(),
                mealLog.getThumbnail().map(MealImage::getImageUrl).orElse(null),
                mealLog.getTotalCalorie());
    }
}
