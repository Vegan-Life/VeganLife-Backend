package com.konggogi.veganlife.meallog.domain.mapper;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.meallog.controller.dto.request.MealAddRequest;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealAddRequest.mealDataId", ignore = true)
    @Mapping(source = "mealLog", target = "mealLog")
    @Mapping(source = "mealData", target = "mealData")
    @Mapping(source = "mealAddRequest.name", target = "name")
    @Mapping(source = "mealAddRequest.intakeUnit", target = "intakeUnit")
    Meal mealAddRequestToEntity(MealAddRequest mealAddRequest, MealLog mealLog, MealData mealData);
}
