package com.konggogi.veganlife.mealdata.domain.mapper;


import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.domain.MealData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealDataMapper {

    MealDataListResponse toMealDataListResponse(MealData mealDataListDto);

    MealDataDetailsResponse toMealDataDetailsResponse(MealData mealDataDetailsDto);
}
