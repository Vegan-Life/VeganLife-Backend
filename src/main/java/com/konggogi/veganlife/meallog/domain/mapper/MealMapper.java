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
    @Mapping(source = "mealAddRequest.name", target = "name")
    @Mapping(source = "mealAddRequest.intakeUnit", target = "intakeUnit")
    Meal toEntity(MealAddRequest mealAddRequest, MealData mealData);

    @Mapping(source = "meal.mealData.type", target = "mealDataType")
    @Mapping(source = "meal.mealData.amount", target = "amount")
    @Mapping(source = "meal.mealData.amountPerServe", target = "amountPerServe")
    MealDetailsResponse toMealDetailsResponse(Meal meal);
}
