package com.konggogi.veganlife.mealdata.domain.mapper;


import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.service.dto.MealDataDetailsDto;
import com.konggogi.veganlife.mealdata.service.dto.MealDataListDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealDataDtoMapper {

    MealDataListResponse toMealDataListResponse(MealDataListDto mealDataListDto);

    MealDataDetailsResponse toMealDataDetailsResponse(MealDataDetailsDto mealDataDetailsDto);
}
