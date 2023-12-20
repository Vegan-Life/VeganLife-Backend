package com.konggogi.veganlife.member.domain.mapper;


import com.konggogi.veganlife.member.controller.dto.response.TodayIntakeResponse;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NutrientsMapper {
    TodayIntakeResponse toTodayIntakeResponse(IntakeNutrients intakeNutrients);
}
