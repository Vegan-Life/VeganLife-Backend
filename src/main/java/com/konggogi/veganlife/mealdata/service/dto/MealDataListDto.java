package com.konggogi.veganlife.mealdata.service.dto;


import com.konggogi.veganlife.mealdata.domain.AccessType;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.PersonalMealData;

public record MealDataListDto(Long id, String name, AccessType accessType) {

    public static MealDataListDto fromMealData(MealData mealData) {

        return new MealDataListDto(mealData.getId(), mealData.getName(), mealData.getAccessType());
    }

    public static MealDataListDto fromPersonalMealData(PersonalMealData mealData) {

        return new MealDataListDto(mealData.getId(), mealData.getName(), mealData.getAccessType());
    }
}
