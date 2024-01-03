package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealModifyRequest;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealMapper {

    @Mapping(target = "id", ignore = true)
    Meal toEntity(MealAddRequest request, MealLog mealLog, MealData mealData);

    @Mapping(target = "id", ignore = true)
    Meal toEntity(MealModifyRequest request, MealLog mealLog, MealData mealData);
}
