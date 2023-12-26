package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.response.MealDetailsResponse;
import com.konggogi.veganlife.meallog.domain.Meal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealLog", ignore = true)
    Meal toEntity(MealAddRequest mealAddRequest, MealData mealData);

    MealDetailsResponse toMealDetailsResponse(Meal meal);
}
