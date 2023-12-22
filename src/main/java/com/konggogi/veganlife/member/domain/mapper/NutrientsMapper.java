package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.response.CalorieIntakeResponse;
import com.konggogi.veganlife.member.controller.dto.response.TodayIntakeResponse;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NutrientsMapper {
    TodayIntakeResponse toTodayIntakeResponse(IntakeNutrients intakeNutrients);

    @Mapping(source = "caloriesOfMealTypes", target = "weekly")
    CalorieIntakeResponse toCalorieIntakeResponse(
            int totalCalorie, List<CaloriesOfMealType> caloriesOfMealTypes);
}
