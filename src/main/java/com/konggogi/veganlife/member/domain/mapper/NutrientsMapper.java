package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.response.CalorieIntakeResponse;
import com.konggogi.veganlife.member.controller.dto.response.DailyIntakeResponse;
import com.konggogi.veganlife.member.controller.dto.response.RecommendNutrientsResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeCalorie;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NutrientsMapper {
    DailyIntakeResponse toDailyIntakeResponse(IntakeNutrients intakeNutrients);

    @Mapping(target = "dailyCalorie", source = "member.AMR")
    RecommendNutrientsResponse toRecommendNutrientsResponse(Member member);

    default CalorieIntakeResponse toCalorieIntakeResponse(List<IntakeCalorie> intakeCalories) {
        int totalCalorie = intakeCalories.stream().mapToInt(IntakeCalorie::getTotalCalorie).sum();
        return new CalorieIntakeResponse(totalCalorie, intakeCalories);
    }
}
